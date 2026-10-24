package com.microbond.app.core.components.threadbubbles.itemscreens.screens.contactscreens

internal data class DeviceContact(
    val id: Long,
    val name: String,
    val phone: String,
    val photoUri: String?
) {
    val key: String get() = "$id:$phone"
}