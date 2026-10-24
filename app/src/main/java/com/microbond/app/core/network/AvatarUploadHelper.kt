package com.microbond.app.core.network

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

/**
 * Helper to process and prepare user avatars for Cloudflare R2 upload via POST /api/v1/users/me/avatar.
 * Enforces MIME type check (JPEG/PNG/WebP) and the 5 MB maximum size limit.
 */
object AvatarUploadHelper {

    fun createAvatarMultipartPart(context: Context, imageUri: Uri): MultipartBody.Part? {
        return try {
            val contentResolver = context.contentResolver
            val mimeType = contentResolver.getType(imageUri) ?: "image/jpeg"
            val validMimeType = when {
                mimeType.contains("png", ignoreCase = true) -> "image/png"
                mimeType.contains("webp", ignoreCase = true) -> "image/webp"
                else -> "image/jpeg"
            }
            val extension = when (validMimeType) {
                "image/png" -> "png"
                "image/webp" -> "webp"
                else -> "jpg"
            }

            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, imageUri))
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
            } ?: return null

            val outputStream = ByteArrayOutputStream()
            val format = if (validMimeType == "image/png") {
                Bitmap.CompressFormat.PNG
            } else {
                Bitmap.CompressFormat.JPEG
            }
            bitmap.compress(format, 90, outputStream)
            val bytes = outputStream.toByteArray()

            // 5 MB limit
            if (bytes.size > 5 * 1024 * 1024) return null

            val requestBody = bytes.toRequestBody(validMimeType.toMediaTypeOrNull())
            MultipartBody.Part.createFormData("file", "avatar.$extension", requestBody)
        } catch (_: Exception) {
            null
        }
    }
}
