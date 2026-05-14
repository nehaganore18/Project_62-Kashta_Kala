package com.example.kashtakala

import android.content.Context
import android.content.SharedPreferences

object UserManager {
    private const val PREFS_NAME = "kashta_kala_prefs"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    private const val KEY_IS_ADMIN = "is_admin"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun login(context: Context, isAdmin: Boolean) {
        getPrefs(context).edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putBoolean(KEY_IS_ADMIN, isAdmin)
            apply()
        }
    }

    fun logout(context: Context) {
        getPrefs(context).edit().clear().apply()
    }

    fun isLoggedIn(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun isAdmin(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_IS_ADMIN, false)
    }
}