package com.microbond.app.core.data_classes_and_enums

data class DocumentData(
    val fileName: String,
    val fileType: String,
    val fileSize: String,
    val pageCount: Int? = null,
    val uri: String? = null,
    val dateModified: Long? = null
)
