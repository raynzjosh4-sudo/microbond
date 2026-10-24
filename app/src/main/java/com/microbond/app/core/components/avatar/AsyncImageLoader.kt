package com.microbond.app.core.components.avatar

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.LruCache
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.InputStream
import java.util.concurrent.TimeUnit

/**
 * Lightweight, high-performance in-memory image loader and cache for avatars and chat media.
 * Supports HTTP/HTTPS network URLs, content:// URIs, and file:// URIs without external heavy libraries.
 */
object AsyncImageLoader {

    private val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
    private val cacheSize = (maxMemory / 8).coerceAtLeast(1024 * 16) // 1/8th of available memory

    private val memoryCache = object : LruCache<String, Bitmap>(cacheSize) {
        override fun sizeOf(key: String, bitmap: Bitmap): Int {
            return bitmap.byteCount / 1024
        }
    }

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .followRedirects(true)
            .build()
    }

    fun getFromCache(key: String): Bitmap? = synchronized(memoryCache) {
        memoryCache.get(key)
    }

    fun putInCache(key: String, bitmap: Bitmap) = synchronized(memoryCache) {
        memoryCache.put(key, bitmap)
    }

    suspend fun loadBitmap(context: Context, urlOrUri: String, maxDimension: Int = 1024): Bitmap? {
        val trimmed = urlOrUri.trim()
        if (trimmed.isBlank()) return null

        getFromCache(trimmed)?.let { return it }

        return withContext(Dispatchers.IO) {
            try {
                val bitmap = when {
                    trimmed.startsWith("http://", ignoreCase = true) ||
                    trimmed.startsWith("https://", ignoreCase = true) -> {
                        loadFromNetwork(trimmed, maxDimension)
                    }
                    trimmed.startsWith("file://", ignoreCase = true) -> {
                        val path = trimmed.removePrefix("file://").removePrefix("file:")
                        loadFromFile(java.io.File(path), maxDimension)
                    }
                    trimmed.startsWith("content://", ignoreCase = true) -> {
                        loadFromContentUri(context, Uri.parse(trimmed), maxDimension)
                    }
                    trimmed.startsWith("/") && java.io.File(trimmed).exists() -> {
                        loadFromFile(java.io.File(trimmed), maxDimension)
                    }
                    trimmed.startsWith("/") ||
                    trimmed.startsWith("uploads/", ignoreCase = true) ||
                    trimmed.startsWith("media/", ignoreCase = true) ||
                    trimmed.startsWith("static/", ignoreCase = true) ||
                    trimmed.startsWith("api/", ignoreCase = true) -> {
                        val base = com.microbond.app.BuildConfig.BASE_URL.trimEnd('/')
                        val fullUrl = if (trimmed.startsWith("/")) "$base$trimmed" else "$base/$trimmed"
                        loadFromNetwork(fullUrl, maxDimension)
                    }
                    trimmed.toIntOrNull() != null -> {
                        loadFromDrawableRes(context, trimmed.toInt(), maxDimension)
                    }
                    else -> {
                        val file = java.io.File(trimmed)
                        if (file.exists()) {
                            loadFromFile(file, maxDimension)
                        } else {
                            loadFromContentUri(context, Uri.parse(trimmed), maxDimension)
                        }
                    }
                }

                if (bitmap != null) {
                    putInCache(trimmed, bitmap)
                }
                bitmap
            } catch (e: Exception) {
                android.util.Log.w("AsyncImageLoader", "Failed to load image from $trimmed: ${e.message}")
                null
            }
        }
    }

    private fun loadFromFile(file: java.io.File, maxDimension: Int): Bitmap? {
        if (!file.exists() || file.length() == 0L) return null
        return try {
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(file.absolutePath, options)

            options.inSampleSize = calculateInSampleSize(options, maxDimension, maxDimension)
            options.inJustDecodeBounds = false

            BitmapFactory.decodeFile(file.absolutePath, options)
        } catch (e: Exception) {
            android.util.Log.w("AsyncImageLoader", "loadFromFile failed for ${file.path}: ${e.message}")
            null
        }
    }

    private fun loadFromDrawableRes(context: Context, resId: Int, maxDimension: Int): Bitmap? {
        return try {
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeResource(context.resources, resId, options)

            options.inSampleSize = calculateInSampleSize(options, maxDimension, maxDimension)
            options.inJustDecodeBounds = false

            BitmapFactory.decodeResource(context.resources, resId, options)
        } catch (_: Exception) {
            null
        }
    }

    private fun loadFromNetwork(url: String, maxDimension: Int): Bitmap? {
        val request = Request.Builder().url(url).build()
        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return null
            val bytes = response.body?.bytes() ?: return null

            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)

            options.inSampleSize = calculateInSampleSize(options, maxDimension, maxDimension)
            options.inJustDecodeBounds = false

            return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
        }
    }

    private fun loadFromContentUri(context: Context, uri: Uri, maxDimension: Int): Bitmap? {
        return try {
            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                ?: return null
            if (bytes.isEmpty()) return null

            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)

            options.inSampleSize = calculateInSampleSize(options, maxDimension, maxDimension)
            options.inJustDecodeBounds = false

            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
        } catch (e: Exception) {
            android.util.Log.w("AsyncImageLoader", "loadFromContentUri failed for $uri: ${e.message}")
            null
        }
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}

@Composable
fun rememberAsyncBitmap(urlOrUri: String?, maxDimension: Int = 1024): Bitmap? {
    val context = LocalContext.current.applicationContext
    if (urlOrUri.isNullOrBlank()) return null

    val cached = AsyncImageLoader.getFromCache(urlOrUri.trim())
    return produceState<Bitmap?>(initialValue = cached, key1 = urlOrUri) {
        if (value == null) {
            value = AsyncImageLoader.loadBitmap(context, urlOrUri, maxDimension)
        }
    }.value
}

@Composable
fun rememberAsyncImagePainter(urlOrUri: String?, maxDimension: Int = 1024): Painter? {
    val bitmap = rememberAsyncBitmap(urlOrUri, maxDimension)
    return bitmap?.let { BitmapPainter(it.asImageBitmap()) }
}
