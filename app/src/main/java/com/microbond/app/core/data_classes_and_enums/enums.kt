package com.microbond.app.core.data_classes_and_enums

import androidx.compose.ui.graphics.Color

enum class GroupSavingsType {
    LONG_TERM,
    SHORT_TERM
}

enum class MemberSaveStatus {
    SAVED, //Green ring segment
    MISSED, //Red ring segment
    PENDING // Dark gray segment
}

// status ticks enums
enum class MessageStatus {
    PENDING,
    SENT,
    DELIVERED,
    READ,
    FAILED
}