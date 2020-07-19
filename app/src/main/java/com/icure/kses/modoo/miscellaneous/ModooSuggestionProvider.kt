package com.icure.kses.modoo.miscellaneous

import android.content.SearchRecentSuggestionsProvider

class ModooSuggestionProvider : SearchRecentSuggestionsProvider() {

    companion object{
        const val AUTHORITY: String = "com.icure.kses.modoo.miscellaneous.ModooSuggestionProvider"
        const val MODE: Int = DATABASE_MODE_QUERIES
    }

    init {
        setupSuggestions(AUTHORITY, MODE)
    }
}