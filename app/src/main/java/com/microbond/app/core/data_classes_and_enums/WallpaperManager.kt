package com.microbond.app.core.data_classes_and_enums

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object WallpaperManager {
    private const val PREFS_NAME = "wallpaper_prefs"
    private const val KEY_WALLPAPER = "selected_wallpaper"

    private val _selectedWallpaper = MutableStateFlow<Int?>(null)
    val selectedWallpaper: StateFlow<Int?> = _selectedWallpaper

    fun load(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val resId = prefs.getInt(KEY_WALLPAPER, 0)
        _selectedWallpaper.value = if (resId != 0) resId else null
    }

    fun setWallpaper(context: Context, resId: Int?) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit {
            if (resId != null) {
                putInt(KEY_WALLPAPER, resId)
            } else {
                remove(KEY_WALLPAPER)
            }
        }
        _selectedWallpaper.value = resId
    }
}
