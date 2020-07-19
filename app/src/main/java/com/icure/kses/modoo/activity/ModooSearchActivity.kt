package com.icure.kses.modoo.activity

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.SearchRecentSuggestions
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.icure.kses.modoo.R
import com.icure.kses.modoo.miscellaneous.ModooSuggestionProvider
import kotlinx.android.synthetic.main.activity_search_result.*

class ModooSearchActivity : AppCompatActivity() {

    var suggestions: SearchRecentSuggestions? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        suggestions = SearchRecentSuggestions(this, ModooSuggestionProvider.AUTHORITY, ModooSuggestionProvider.MODE)

        sv_search.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        sv_search.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                sv_search.setQuery("", false)
                sv_search.clearFocus()
                suggestions?.saveRecentQuery(query, null)
                Log.i("tagg","query : " + query)
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    return it.length > 0
                }
                return false
            }
        })
        sv_search.setOnSuggestionListener(object : SearchView.OnSuggestionListener{
            override fun onSuggestionSelect(position: Int): Boolean {
                return true
            }
            override fun onSuggestionClick(position: Int): Boolean {
                val cursor: Cursor = sv_search.suggestionsAdapter.getItem(position) as Cursor
                val index:Int = cursor.getColumnIndexOrThrow(SearchManager.SUGGEST_COLUMN_TEXT_1)
                sv_search.setQuery(cursor.getString(index), true)
                return true
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }
}