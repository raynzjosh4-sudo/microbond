package com.microbond.app.core.network

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream

import com.microbond.app.core.media.MediaCompressor

/**
 * Prepares files (images, videos, audio/voice notes, documents) from content:// or file:// URIs
 * for uploading to backend group chat endpoints.
 * Automatically compresses high-resolution photos via [MediaCompressor].
 */
object MediaUploadHelper {

    fun createMultipartPart(context: Context, uri: Uri, partName: String = "files"): MultipartBody.Part? {
        return MediaCompressor.createCompressedMultipartPart(context, uri, partName)
    }
}
