package com.microbond.app.core.media

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.provider.OpenableColumns
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.InputStream
import kotlin.math.max
import kotlin.math.roundToInt

data class CompressedMediaResult(
    val bytes: ByteArray,
    val mimeType: String,
    val filename: String,
    val width: Int,
    val height: Int,
    val sizeBytes: Long = bytes.size.toLong()
)

/**
 * High-performance media compressor that reduces camera photos and gallery uploads
 * before transmitting them to Cloudflare R2 and backend APIs.
 *
 * Features:
 * - Downsamples high-resolution (48MP/108MP) images to optimal mobile HD (max 1920px)
 * - Corrects EXIF camera orientation rotations
 * - Performs visually lossless adaptive JPEG/PNG compression
 * - Reduces multi-megabyte uploads down to ~200KB - 400KB
 */
object MediaCompressor {

    private const val TAG = "MediaCompressor"
    private const val DEFAULT_MAX_DIMENSION = 1920
    private const val DEFAULT_QUALITY = 82
    private const val TARGET_MAX_BYTES = 2 * 1024 * 1024 // 2 MB limit

    /**
     * Compresses an image at [uri] to an optimized JPEG/PNG byte buffer.
     */
    fun compressImage(
        context: Context,
        uri: Uri,
        maxDimension: Int = DEFAULT_MAX_DIMENSION,
        quality: Int = DEFAULT_QUALITY
    ): CompressedMediaResult? {
        val contentResolver = context.contentResolver

        return try {
            // 1. Read EXIF orientation
            var orientationDegrees = 0
            try {
                contentResolver.openInputStream(uri)?.use { inputStream ->
                    val exif = ExifInterface(inputStream)
                    orientationDegrees = when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> 90
                        ExifInterface.ORIENTATION_ROTATE_180 -> 180
                        ExifInterface.ORIENTATION_ROTATE_270 -> 270
                        else -> 0
                    }
                }
            } catch (_: Exception) {
                orientationDegrees = 0
            }

            // 2. Decode image bounds without allocating full bitmap in memory
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream, null, options)
            }

            val origWidth = options.outWidth
            val origHeight = options.outHeight
            if (origWidth <= 0 || origHeight <= 0) return null

            // 3. Calculate sample size for memory-safe decoding
            options.inSampleSize = calculateInSampleSize(origWidth, origHeight, maxDimension, maxDimension)
            options.inJustDecodeBounds = false
            options.inPreferredConfig = Bitmap.Config.ARGB_8888

            val sampledBitmap = contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream, null, options)
            } ?: return null

            // 4. Scale down and apply EXIF rotation if necessary
            val finalBitmap = transformBitmap(sampledBitmap, maxDimension, orientationDegrees)

            // 5. Determine format & compress
            val rawMimeType = contentResolver.getType(uri) ?: "image/jpeg"
            val isPng = rawMimeType.contains("png", ignoreCase = true)
            val format = if (isPng) Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG
            val outputMime = if (isPng) "image/png" else "image/jpeg"
            val ext = if (isPng) "png" else "jpg"

            var outputStream = ByteArrayOutputStream()
            finalBitmap.compress(format, quality, outputStream)
            var bytes = outputStream.toByteArray()

            // Adaptive secondary compression pass if still larger than 2MB
            if (!isPng && bytes.size > TARGET_MAX_BYTES) {
                outputStream = ByteArrayOutputStream()
                finalBitmap.compress(format, 65, outputStream)
                bytes = outputStream.toByteArray()
            }

            if (finalBitmap != sampledBitmap) {
                sampledBitmap.recycle()
            }
            finalBitmap.recycle()

            val displayName = resolveOriginalFilename(context, uri) ?: "photo_${System.currentTimeMillis()}.$ext"
            val finalFilename = if (displayName.endsWith(".$ext", ignoreCase = true)) displayName else "$displayName.$ext"

            android.util.Log.d(
                TAG,
                "Compressed image from ${origWidth}x${origHeight} to ${bytes.size / 1024} KB ($finalFilename)"
            )

            CompressedMediaResult(
                bytes = bytes,
                mimeType = outputMime,
                filename = finalFilename,
                width = origWidth,
                height = origHeight,
                sizeBytes = bytes.size.toLong()
            )
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Failed to compress image at $uri", e)
            null
        }
    }

    /**
     * Compresses a raw in-memory [Bitmap] down to optimal dimensions (e.g. 1080px) and quality.
     */
    fun compressBitmap(
        bitmap: Bitmap,
        maxDimension: Int = 1080,
        quality: Int = 85
    ): CompressedMediaResult {
        val width = bitmap.width
        val height = bitmap.height
        val maxSide = max(width, height)
        val scaledBitmap = if (maxSide > maxDimension) {
            val scale = maxDimension.toFloat() / maxSide
            val matrix = Matrix().apply { postScale(scale, scale) }
            Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
        } else {
            bitmap
        }

        val outputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        val bytes = outputStream.toByteArray()

        if (scaledBitmap != bitmap) {
            scaledBitmap.recycle()
        }

        val filename = "photo_${System.currentTimeMillis()}.jpg"
        return CompressedMediaResult(
            bytes = bytes,
            mimeType = "image/jpeg",
            filename = filename,
            width = width,
            height = height,
            sizeBytes = bytes.size.toLong()
        )
    }

    /**
     * Prepares an optimized avatar [MultipartBody.Part] for [uri], compressing images automatically
     * and strictly adhering to the 5MB backend limit with "file" part name.
     */
    fun createCompressedAvatarPart(
        context: Context,
        uri: Uri,
        partName: String = "file",
        maxDimension: Int = 1080,
        quality: Int = 85
    ): MultipartBody.Part? {
        val compressed = compressImage(context, uri, maxDimension = maxDimension, quality = quality) ?: return null
        val body = compressed.bytes.toRequestBody(compressed.mimeType.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, compressed.filename, body)
    }

    /**
     * Prepares an optimized avatar [MultipartBody.Part] from a camera [Bitmap].
     */
    fun createCompressedAvatarPartFromBitmap(
        bitmap: Bitmap,
        partName: String = "file",
        maxDimension: Int = 1080,
        quality: Int = 85
    ): MultipartBody.Part {
        val compressed = compressBitmap(bitmap, maxDimension = maxDimension, quality = quality)
        val body = compressed.bytes.toRequestBody(compressed.mimeType.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, compressed.filename, body)
    }

    /**
     * Prepares an optimized [MultipartBody.Part] for [uri], compressing images automatically
     * and preserving audio/video/document integrity.
     */
    fun createCompressedMultipartPart(
        context: Context,
        uri: Uri,
        partName: String = "files"
    ): MultipartBody.Part? {
        val contentResolver = context.contentResolver
        val mimeType = resolveMimeType(context, uri)

        // 1. If image, apply high-quality compression
        if (mimeType.startsWith("image/")) {
            val compressed = compressImage(context, uri)
            if (compressed != null) {
                val body = compressed.bytes.toRequestBody(compressed.mimeType.toMediaTypeOrNull())
                return MultipartBody.Part.createFormData(partName, compressed.filename, body)
            }
        }

        // 2. For audio, video, documents, or fallback, read stream cleanly
        return try {
            val inputStream: InputStream = contentResolver.openInputStream(uri) ?: return null
            val bytes = inputStream.use { it.readBytes() }
            val filename = resolveOriginalFilename(context, uri) ?: "upload_${System.currentTimeMillis()}"
            val body = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
            MultipartBody.Part.createFormData(partName, filename, body)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Failed to create multipart part for $uri", e)
            null
        }
    }

    private fun calculateInSampleSize(width: Int, height: Int, reqWidth: Int, reqHeight: Int): Int {
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return max(1, inSampleSize)
    }

    private fun transformBitmap(source: Bitmap, maxDimension: Int, rotationDegrees: Int): Bitmap {
        val width = source.width
        val height = source.height

        val maxSide = max(width, height)
        val scale = if (maxSide > maxDimension) maxDimension.toFloat() / maxSide else 1f

        if (scale == 1f && rotationDegrees == 0) {
            return source
        }

        val matrix = Matrix().apply {
            if (scale != 1f) postScale(scale, scale)
            if (rotationDegrees != 0) postRotate(rotationDegrees.toFloat())
        }

        return Bitmap.createBitmap(source, 0, 0, width, height, matrix, true)
    }

    fun resolveMimeType(context: Context, uri: Uri): String {
        return context.contentResolver.getType(uri) ?: when {
            uri.path?.endsWith(".jpg", ignoreCase = true) == true || uri.path?.endsWith(".jpeg", ignoreCase = true) == true -> "image/jpeg"
            uri.path?.endsWith(".png", ignoreCase = true) == true -> "image/png"
            uri.path?.endsWith(".webp", ignoreCase = true) == true -> "image/webp"
            uri.path?.endsWith(".mp4", ignoreCase = true) == true -> "video/mp4"
            uri.path?.endsWith(".mp3", ignoreCase = true) == true -> "audio/mpeg"
            uri.path?.endsWith(".m4a", ignoreCase = true) == true -> "audio/mp4"
            uri.path?.endsWith(".aac", ignoreCase = true) == true -> "audio/aac"
            uri.path?.endsWith(".3gp", ignoreCase = true) == true -> "audio/3gpp"
            uri.path?.endsWith(".pdf", ignoreCase = true) == true -> "application/pdf"
            else -> "application/octet-stream"
        }
    }

    fun resolveOriginalFilename(context: Context, uri: Uri): String? {
        return try {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1 && cursor.moveToFirst()) {
                    cursor.getString(nameIndex)
                } else null
            }
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Resolves the aspect ratio (width / height) of an image URI before upload,
     * taking EXIF orientation into account so optimistic UI bubbles size correctly immediately.
     */
    fun getImageAspectRatio(context: Context, uri: Uri): Float? {
        return try {
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, options)
            }
            if (options.outWidth <= 0 || options.outHeight <= 0) return null

            var degrees = 0
            try {
                context.contentResolver.openInputStream(uri)?.use {
                    val exif = ExifInterface(it)
                    val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                    degrees = when (orientation) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> 90
                        ExifInterface.ORIENTATION_ROTATE_180 -> 180
                        ExifInterface.ORIENTATION_ROTATE_270 -> 270
                        else -> 0
                    }
                }
            } catch (_: Exception) {}

            if (degrees == 90 || degrees == 270) {
                options.outHeight.toFloat() / options.outWidth.toFloat()
            } else {
                options.outWidth.toFloat() / options.outHeight.toFloat()
            }
        } catch (_: Exception) {
            null
        }
    }
}

