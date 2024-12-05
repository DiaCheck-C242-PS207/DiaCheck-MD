package com.project.diacheck.data.local.settings

import android.content.Context

object ThemePreference {
    private const val PREFS_NAME = "theme_prefs"
    private const val THEME_KEY = "is_dark_mode"

    fun setDarkMode(context: Context, isDarkMode: Boolean) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(THEME_KEY, isDarkMode).apply()
    }

    fun isDarkMode(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(THEME_KEY, false) // Default to light mode
    }
}
