package com.icure.kses.modoo.utility

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by whan 2020/06/08
 */
class PrefManager(val _context: Context) {
    var pref: SharedPreferences
    var editor: SharedPreferences.Editor

    // shared pref mode
    var PRIVATE_MODE = 0

    var isFirstTimeLaunch: Boolean
        get() = pref.getBoolean(PREFKEY_IS_FIRST_TIME_LAUNCH, true)
        set(isFirstTime) {
            editor.putBoolean(PREFKEY_IS_FIRST_TIME_LAUNCH, isFirstTime)
            editor.commit()
        }

    var fcmAccessToken: String?
        get() = pref.getString(PREFKEY_FCM_ACCESS_TOKEN, null)
        set(accessToken) {
            editor.putString(PREFKEY_FCM_ACCESS_TOKEN, accessToken)
            editor.commit()
        }

    var suggestionWords: String?
        get() = pref.getString(PREFKEY_SUGGESTION_WORDS, "")
        set(words) {
            editor.putString(PREFKEY_SUGGESTION_WORDS, words)
            editor.commit()
        }

    companion object {
        // Shared preferences constants
        private const val PREF_NAME = "modoopref"
        private const val PREFKEY_IS_FIRST_TIME_LAUNCH = "PREFKEY_IS_FIRST_TIME_LAUNCH"
        private const val PREFKEY_FCM_ACCESS_TOKEN = "PREFKEY_FCM_ACCESS_TOKEN"
        private const val PREFKEY_SUGGESTION_WORDS = "PREFKEY_SUGGESTION_WORDS"
    }

    init {
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
    }
}