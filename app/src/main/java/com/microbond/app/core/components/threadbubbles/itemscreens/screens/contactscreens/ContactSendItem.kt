package com.microbond.app.core.components.threadbubbles.itemscreens.screens.contactscreens

data class ContactSendItem(
    val id: Long,
    val name: String,
    val phone: String,
    val photoUri: String?
) {
    val key: String get() = "$id:$phone"
}