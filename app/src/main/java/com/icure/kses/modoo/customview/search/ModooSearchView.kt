package com.icure.kses.modoo.customview.search

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.database.Cursor
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.icure.kses.modoo.R
import com.icure.kses.modoo.database.ModooDatabaseHelper
import com.icure.kses.modoo.vo.SuggestionItem
import kotlinx.android.synthetic.main.search_view.view.*

import java.util.*


class ModooSearchView @JvmOverloads constructor(private val mContext: Context, attributeSet: AttributeSet? = null, defStyleAttributes: Int = 0) : FrameLayout(mContext, attributeSet) {

    var isOpen = false
        private set

    /**
     * Whether or not the MaterialSearchView will animate into view or just appear.
     */
    private var mShouldAnimate = true

    /**
     * Whether or not the MaterialSearchView will clonse under a click on the Tint View (Blank Area).
     */
    private var mShouldCloseOnTintClick = false

    /**
     * Wheter to keep the search history or not.
     */
    private var mShouldKeepHistory = true

    /**
     * Flag for whether or not we are clearing focus.
     */
    private var mClearingFocus = false

    /**
     * Voice hint prompt text.
     */
    private var mHintPrompt: String? = null

    /**
     * Determines if we can use the voice icon.
     * @return True if the icon is visible, false otherwise.
     */
    /**
     * Set whether or not we should allow voice search.
     * @param enabled True if we want to allow searching by voice, false if not.
     */
    /**
     * Allows user to decide whether to allow voice search.
     */
    var isVoiceIconEnabled = false
    //endregion
    //region UI Elements
    /**
     * The tint that appears over the search view.
     */
    private var mTintView: View? = null

    /**
     * The root of the search view.
     */
//    private var mRoot: FrameLayout? = null

    /**
     * The bar at the top of the SearchView containing the EditText and ImageButtons.
     */
    private var mSearchBar: LinearLayout? = null

    /**
     * The EditText for entering a search.
     */
    private var mSearchEditText: EditText? = null

    /**
     * The ImageButton for navigating back.
     */
    private var mBack: ImageButton? = null

    /**
     * The ImageButton for initiating a voice search.
     */
//    private var mVoice: ImageButton? = null

    /**
     * The ImageButton for clearing the search text.
     */
    private var mClear: ImageButton? = null

    /**
     * The ListView for displaying suggestions based on the search.
     */
    private var mSuggestionsListView: RecyclerView? = null

    /**
     * Retrieves the adapter.
     */
    /**
     * Adapter for displaying suggestions.
     */
    var adapter: CursorSearchAdapter? = null
    //endregion
    //region Query Properties
    /**
     * The previous query text.
     */
    private var mOldQuery: CharSequence? = null

    /**
     * The current query text.
     */
    private var mCurrentQuery: CharSequence? = null
    //endregion
    //region Listeners
    /**
     * Listener for when the query text is submitted or changed.
     */
    private var mOnQueryTextListener: OnQueryTextListener? = null

    /**
     * Listener for when the search view opens and closes.
     */
    private var mSearchViewListener: SearchViewListener? = null

    /**
     * Listener for interaction with the voice button.
     */
    private var mOnVoiceClickedListener: OnVoiceClickedListener? = null
    //endregion
    //region Initializers

    private var mRoot: FrameLayout? = null
    /**
     * Preforms any required initializations for the search view.
     */
    private fun init() {
        // Inflate view
        try {
            Log.i("tagg", "start init")
            var view:View = LayoutInflater.from(mContext).inflate(R.layout.search_view, this, true)

            // Get items
            mRoot = view.findViewById(R.id.search_layout)
            mTintView = mRoot?.findViewById(R.id.transparent_view)
            mSearchBar = mRoot?.findViewById(R.id.search_bar)
            mBack = mRoot?.findViewById(R.id.action_back)
            mSearchEditText = mRoot?.findViewById(R.id.et_search)
//        mVoice = search_layout.findViewById(R.id.action_voice)
            mClear = mRoot?.findViewById(R.id.action_clear)
            mSuggestionsListView = mRoot?.findViewById(R.id.suggestion_list)
            mSuggestionsListView?.setHasFixedSize(true)

            // Set click listeners
            mBack?.setOnClickListener(OnClickListener { closeSearch() })
//        mVoice?.setOnClickListener(OnClickListener { onVoiceClicked() })
            mClear?.setOnClickListener(object : OnClickListener {
                override fun onClick(v: View) {
                    mSearchEditText?.setText("")
                    adapter!!.setLastFilterQuery("")
                }
            })
            mTintView?.setOnClickListener(OnClickListener {
                if (mShouldCloseOnTintClick) {
                    closeSearch()
                }
            })

            initSearchView()
            adapter = CursorSearchAdapter(mContext, historyList, this)
            val staggeredGridLayoutManager: RecyclerView.LayoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            mSuggestionsListView?.setAdapter(adapter)
            mSuggestionsListView?.setLayoutManager(staggeredGridLayoutManager)
        }catch(e:Exception){
            Log.i("tagg", "init ERROR : ${e}")
        }
    }

    var itemClickListener: (View, Int) -> Unit = {v, i ->}

    /**
     * Preforms necessary initializations on the SearchView.
     */
    private fun initSearchView() {
        mSearchEditText!!.setOnEditorActionListener { v, actionId, event -> // When an edit occurs, submit the query.
            onSubmitQuery()
            true
        }
        mSearchEditText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // When the text changes, filter
                val items: MutableList<SuggestionItem>?
                val filter = s.toString()
                adapter?.setLastFilterQuery(mSearchEditText?.text.toString())
                items = if (filter.isEmpty()) {
                    historyList
                } else {
                    searchQueryList(filter)
                }
                items?.let { adapter?.notifyDataSetChanged(it) }
                this@ModooSearchView.onTextChanged(s)
            }

            override fun afterTextChanged(s: Editable) {}
        })
        mSearchEditText!!.onFocusChangeListener = OnFocusChangeListener { v, hasFocus -> // If we gain focus, show keyboard and show suggestions.
            if (hasFocus) {
                showKeyboard(mSearchEditText)
                showSuggestions()
            }
        }
    }
    //endregion
    //region Show Methods
    /**
     * Displays the keyboard with a focus on the Search EditText.
     * @param view The view to attach the keyboard to.
     */
    private fun showKeyboard(view: View?) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1 && view!!.hasFocus()) {
            view.clearFocus()
        }
        view!!.requestFocus()
        if (!isHardKeyboardAvailable) {
            val inputMethodManager = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(view, 0)
        }
    }

    /**
     * Method that checks if there's a physical keyboard on the phone.
     *
     * @return true if there's a physical keyboard connected, false otherwise.
     */
    private val isHardKeyboardAvailable: Boolean
        private get() = mContext.resources.configuration.keyboard != Configuration.KEYBOARD_NOKEYS

    /**
     * Changes the visibility of the clear button to VISIBLE or GONE.
     * @param display True to display the clear button, false to hide it.
     */
    private fun displayClearButton(display: Boolean) {
        mClear!!.visibility = if (display) View.VISIBLE else View.GONE
    }

    /**
     * Displays the available suggestions, if any.
     */
    private fun showSuggestions() {
        mSuggestionsListView!!.visibility = View.VISIBLE
    }

    /**
     * Displays the SearchView.
     */
    fun openSearch() {
        // If search is already open, just return.
        if (isOpen) {
            return
        }

        // Get focus
        mSearchEditText!!.setText("")
        mSearchEditText!!.requestFocus()
        adapter!!.setLastFilterQuery("")
        search_layout.visibility = View.VISIBLE
        if (mSearchViewListener != null) {
            mSearchViewListener!!.onSearchViewOpened()
        }
        isOpen = true
    }
    //endregion
    //region Hide Methods
    /**
     * Hides the suggestion list.
     */
    private fun dismissSuggestions() {
        mSuggestionsListView!!.visibility = View.GONE
    }

    /**
     * Hides the keyboard displayed for the SearchEditText.
     * @param view The view to detach the keyboard from.
     */
    private fun hideKeyboard(view: View) {
        val inputMethodManager = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * Closes the search view if necessary.
     */
    fun closeSearch() {
        // If we're already closed, just return.
        if (!isOpen) {
            return
        }

        // Clear text, values, and focus.
        mSearchEditText?.setText("")
        adapter?.setLastFilterQuery("")
        dismissSuggestions()
        clearFocus()

        search_layout.visibility = View.GONE

        // Call listener if we have one
        if (mSearchViewListener != null) {
            mSearchViewListener!!.onSearchViewClosed()
        }
        isOpen = false
    }
    //endregion
    //region Interface Methods
    /**
     * Filters and updates the buttons when text is changed.
     * @param newText The new text.
     */
    private fun onTextChanged(newText: CharSequence) {
        // Get current query
        mCurrentQuery = mSearchEditText?.text

        // If the text is not empty, show the empty button and hide the voice button
        if (!TextUtils.isEmpty(mCurrentQuery)) {
            displayClearButton(true)
        } else {
            displayClearButton(false)
        }

        // If we have a query listener and the text has changed, call it.
        if (mOnQueryTextListener != null) {
            mOnQueryTextListener?.onQueryTextChange(newText.toString())
        }
        mOldQuery = mCurrentQuery
    }

    /**
     * Called when a query is submitted. This will close the search view.
     */
    private fun onSubmitQuery() {
        // Get the query.
        val query: CharSequence? = mSearchEditText?.text

        // If the query is not null and it has some text, submit it.
        if (query != null && TextUtils.getTrimmedLength(query) > 0) {

            // If we don't have a listener, or if the search view handled the query, close it.
            // TODO - Improve.
            if (mOnQueryTextListener == null || !mOnQueryTextListener!!.onQueryTextSubmit(query.toString())) {
                if (mShouldKeepHistory) {
                    saveQueryToDb(query.toString(), System.currentTimeMillis())
                }

                // Refresh the cursor on the adapter,
                // so the new entry will be shown on the next time the user opens the search view.
                refreshAdapterCursor()
                closeSearch()
                mSearchEditText!!.setText("")
                adapter!!.setLastFilterQuery("")
            }
        }
    }

    /**
     * Handles when the voice button is clicked and starts listening, then calls activity with voice search.
     */
//    private fun onVoiceClicked() {
//        // If the user has their own OnVoiceClickedListener defined, call that. Otherwise, use
//        // the library default.
//        if (mOnVoiceClickedListener != null) {
//            mOnVoiceClickedListener!!.onVoiceClicked()
//        } else {
//            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
//            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, mHintPrompt)
//            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
//            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, MAX_RESULTS) // Quantity of results we want to receive
//            if (mContext is Activity) {
//                mContext.startActivityForResult(intent, REQUEST_VOICE)
//            }
//        }
//    }

    //endregion
    //region Mutators
    fun setOnQueryTextListener(mOnQueryTextListener: OnQueryTextListener?) {
        this.mOnQueryTextListener = mOnQueryTextListener
    }

    fun setSearchViewListener(mSearchViewListener: SearchViewListener?) {
        this.mSearchViewListener = mSearchViewListener
    }

    /**
     * Sets an OnItemClickListener to the suggestion list.
     *
     * @param listener - The ItemClickListener.
     */

    /**
     * Sets an OnItemLongClickListener to the suggestion list.
     *
     * @param listener - The ItemLongClickListener.
     */
    //    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener listener) {
    //        mSuggestionsListView.setOnItemLongClickListener(listener);
    //    }
    /**
     * Toggles the Tint click action.
     *
     * @param shouldClose - Whether the tint click should close the search view or not.
     */
    fun setCloseOnTintClick(shouldClose: Boolean) {
        mShouldCloseOnTintClick = shouldClose
    }

    /**
     * Sets whether the MSV should be animated on open/close or not.
     *
     * @param mShouldAnimate - true if you want animations, false otherwise.
     */
    fun setShouldAnimate(mShouldAnimate: Boolean) {
        this.mShouldAnimate = mShouldAnimate
    }

    /**
     * Sets whether the MSV should be keeping track of the submited queries or not.
     *
     * @param keepHistory - true if you want to save the search history, false otherwise.
     */
    fun setShouldKeepHistory(keepHistory: Boolean) {
        mShouldKeepHistory = keepHistory
    }

    /**
     * Set the query to search view. If submit is set to true, it'll submit the query.
     *
     * @param query - The Query value.
     * @param submit - Whether to submit or not the query or not.
     */
    fun setQuery(query: CharSequence?, submit: Boolean) {
        Log.i("tagg", "setQuery : $query")
        mSearchEditText!!.setText(query)
        if (query != null) {
            mSearchEditText!!.setSelection(mSearchEditText!!.length())
            mCurrentQuery = query
        }
        if (submit && !TextUtils.isEmpty(query)) {
            onSubmitQuery()
        }
    }

    /**
     * Sets the background of the SearchView.
     * @param background The drawable to use as a background.
     */
    override fun setBackground(background: Drawable) {
        // Method changed in jelly bean for setting background.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mTintView!!.background = background
        } else {
            mTintView!!.setBackgroundDrawable(background)
        }
    }

    /**
     * Sets the background color of the SearchView.
     *
     * @param color The color to use for the background.
     */
    override fun setBackgroundColor(color: Int) {
        setTintColor(color)
    }

    fun setSearchBarColor(color: Int) {
        // Set background color of search bar.
        mSearchEditText!!.setBackgroundColor(color)
    }

    /**
     * Change the color of the background tint.
     *
     * @param color The new color.
     */
    private fun setTintColor(color: Int) {
        mTintView!!.setBackgroundColor(color)
    }

    /**
     * Sets the alpha value of the background tint.
     * @param alpha The alpha value, from 0 to 255.
     */
    fun setTintAlpha(alpha: Int) {
        if (alpha < 0 || alpha > 255) return
        val d = mTintView!!.background
        if (d is ColorDrawable) {
            val color = d.color
            val newColor = Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color))
            setTintColor(newColor)
        }
    }

    /**
     * Adjust the background tint alpha, based on a percentage.
     *
     * @param factor The factor of the alpha, from 0% to 100%.
     */
    fun adjustTintAlpha(factor: Float) {
        if (factor < 0 || factor > 1.0) return
        val d = mTintView!!.background
        if (d is ColorDrawable) {
            var color = d.color
            color = adjustAlpha(color, factor)
            mTintView!!.setBackgroundColor(color)
        }
    }

    /**
     * Adjust the alpha of a color based on a percent factor.
     *
     * @param color - The color you want to change the alpha value.
     * @param factor - The factor of the alpha, from 0% to 100%.
     * @return The color with the adjusted alpha value.
     */
    private fun adjustAlpha(color: Int, factor: Float): Int {
        if (factor < 0) return color
        val alpha = Math.round(Color.alpha(color) * factor)
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color))
    }

    /**
     * Sets the text color of the EditText.
     * @param color The color to use for the EditText.
     */
    fun setTextColor(color: Int) {
        mSearchEditText!!.setTextColor(color)
    }

    /**
     * Sets the text color of the search hint.
     * @param color The color to be used for the hint text.
     */
    fun setHintTextColor(color: Int) {
        mSearchEditText!!.setHintTextColor(color)
    }

    /**
     * Sets the hint to be used for the search EditText.
     * @param hint The hint to be displayed in the search EditText.
     */
    fun setHint(hint: CharSequence?) {
        mSearchEditText!!.hint = hint
    }

    /**
     * Sets the icon for the voice action.
     * @param resourceId The drawable to represent the voice action.
     */
//    fun setVoiceIcon(resourceId: Int) {
//        mVoice!!.setImageResource(resourceId)
//    }

    /**
     * Sets the icon for the clear action.
     * @param resourceId The resource ID of drawable that will represent the clear action.
     */
    fun setClearIcon(resourceId: Int) {
        mClear!!.setImageResource(resourceId)
    }

    /**
     * Sets the icon for the back action.
     * @param resourceId The resource Id of the drawable that will represent the back action.
     */
    fun setBackIcon(resourceId: Int) {
        mBack!!.setImageResource(resourceId)
    }

    /**
     * Sets the background of the suggestions ListView.
     *
     * @param resource The resource to use as a background for the
     * suggestions listview.
     */
    fun setSuggestionBackground(resource: Int) {
        if (resource > 0) {
            mSuggestionsListView!!.setBackgroundResource(resource)
        }
    }
    /**
     * Changes the default history list icon.
     *
     * @param resourceId The resource id of the new history icon.
     */
    //    public void setHistoryIcon(@DrawableRes int resourceId) {
    //        if (mAdapter instanceof CursorSearchAdapter) {
    //            ((CursorSearchAdapter)mAdapter).setHistoryIcon(resourceId);
    //        }
    //    }
    /**
     * Changes the default suggestion list icon.
     *
     * @param resourceId The resource id of the new suggestion icon.
     */
    //    public void setSuggestionIcon(@DrawableRes int resourceId) {
    //        if (mAdapter instanceof CursorSearchAdapter) {
    //            ((CursorSearchAdapter)mAdapter).setSuggestionIcon(resourceId);
    //        }
    //    }
    /**
     * Changes the default suggestion list item text color.
     *
     * @param color The new color.
     */
    //    public void setListTextColor(int color) {
    //        if (mAdapter instanceof CursorSearchAdapter) {
    //            ((CursorSearchAdapter)mAdapter).setTextColor(color);
    //        }
    //    }
    /**
     * Sets the input type of the SearchEditText.
     *
     * @param inputType The input type to set to the EditText.
     */
    fun setInputType(inputType: Int) {
        mSearchEditText!!.inputType = inputType
    }

    /**
     * Sets a click listener for the voice button.
     */
    fun setOnVoiceClickedListener(listener: OnVoiceClickedListener?) {
        mOnVoiceClickedListener = listener
    }

    /**
     * Sets the bar height if prefered to not use the existing actionbar height value
     *
     * @param height The value of the height in pixels
     */
    fun setSearchBarHeight(height: Int) {
        mSearchBar!!.minimumHeight = height
        mSearchBar!!.layoutParams.height = height
    }

    fun setVoiceHintPrompt(hintPrompt: String?) {
        mHintPrompt = if (!TextUtils.isEmpty(hintPrompt)) {
            hintPrompt
        } else {
            mContext.getString(R.string.search_hint)
        }
    }

    /**
     * Returns the actual AppCompat ActionBar height value. This will be used as the default
     *
     * @return The value of the actual actionbar height in pixels
     */
    private val appCompatActionBarHeight: Int
        private get() {
            val tv = TypedValue()
            context.theme.resolveAttribute(R.attr.actionBarSize, tv, true)
            return resources.getDimensionPixelSize(tv.resourceId)
        }

    //endregion
    //region Accessors

    /**
     * Gets the current text on the SearchView, if any. Returns an empty String if no text is available.
     * @return The current query, or an empty String if there's no query.
     */
    val currentQuery: String
        get() = if (!TextUtils.isEmpty(mCurrentQuery)) {
            mCurrentQuery.toString()
        } else ""// Get package manager

    // Gets a list of activities that can handle this intent.

    // Returns true if we have at least one activity.

    /** Determines if the user's voice is available
     * @return True if we can collect the user's voice, false otherwise.
     */
    private val isVoiceAvailable: Boolean
        private get() {
            // Get package manager
            val packageManager = mContext.packageManager

            // Gets a list of activities that can handle this intent.
            val activities = packageManager.queryIntentActivities(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0)

            // Returns true if we have at least one activity.
            return activities.size > 0
        }

    /**
     * Retrieves a suggestion at a given index in the adapter.
     *
     * @return The search suggestion for that index.
     */
    fun getSuggestionAtPosition(position: Int): String {
        // If position is out of range just return empty string.
        return if (position < 0 || position >= adapter!!.itemCount) {
            ""
        } else {
            adapter!!.getItem(position).toString()
        }
    }

    //endregion
    //region View Methods
    /**
     * Handles any cleanup when focus is cleared from the view.
     */
    override fun clearFocus() {
        mClearingFocus = true
        hideKeyboard(this)
        super.clearFocus()
        mSearchEditText!!.clearFocus()
        mClearingFocus = false
    }

    override fun requestFocus(direction: Int, previouslyFocusedRect: Rect): Boolean {
        // Don't accept if we are clearing focus, or if the view isn't focusable.
        return !(mClearingFocus || !isFocusable) && mSearchEditText!!.requestFocus(direction, previouslyFocusedRect)
    }

    //----- Lifecycle methods -----//
    //    public void activityPaused() {
    //        Cursor cursor = ((CursorAdapter)mAdapter).getCursor();
    //        if (cursor != null && !cursor.isClosed()) {
    //            cursor.close();
    //        }
    //    }
    fun activityResumed() {
        refreshAdapterCursor()
    }
    //endregion
    //region Database Methods
    /**
     * Save a query to the local database.
     *
     * @param query - The query to be saved. Can't be empty or null.
     * @param ms - The insert date, in millis. As a suggestion, use System.currentTimeMillis();
     */
    @Synchronized
    fun saveQueryToDb(query: String?, ms: Long) {
        databaseHelper?.insertSuggestion(query, ms)
    }

    /**
     * Add a single suggestion item to the suggestion list.
     * @param suggestion - The suggestion to be inserted on the database.
     */
    @Synchronized
    fun addSuggestion(suggestion: String?) {
        if (!TextUtils.isEmpty(suggestion)) {
            val value = ContentValues()
            value.put(HistoryContract.HistoryEntry.COLUMN_QUERY, suggestion)
            value.put(HistoryContract.HistoryEntry.COLUMN_INSERT_DATE, System.currentTimeMillis())
            value.put(HistoryContract.HistoryEntry.COLUMN_IS_HISTORY, 0) // Saving as suggestion.
            mContext.contentResolver.insert(
                    HistoryContract.HistoryEntry.CONTENT_URI,
                    value
            )
        }
    }

    /**
     * Removes a single suggestion from the list. <br></br>
     * Disclaimer, this doesn't remove a single search history item, only suggestions.
     * @param suggestion - The suggestion to be removed.
     */
    @Synchronized
    fun removeSuggestion(suggestion: String) {
        databaseHelper?.removeSuggestion(suggestion)
    }

    /**
     * Removes a single history from the list. <br></br>
     * @param history - The history to be removed.
     */
    @Synchronized
    fun removeHistory(history: String) {
        if (!TextUtils.isEmpty(history)) {
            mContext.contentResolver.delete(
                    HistoryContract.HistoryEntry.CONTENT_URI,
                    HistoryContract.HistoryEntry.TABLE_NAME +
                            "." +
                            HistoryContract.HistoryEntry.COLUMN_QUERY +
                            " = ? AND " +
                            HistoryContract.HistoryEntry.TABLE_NAME +
                            "." +
                            HistoryContract.HistoryEntry.COLUMN_IS_HISTORY +
                            " = ?"
                    , arrayOf(history, 1.toString()))
        }
    }

    @Synchronized
    fun addSuggestions(suggestions: List<String?>) {
        databaseHelper?.insertSuggestions(suggestions)
    }

    fun addSuggestions(suggestions: Array<String?>) {
        val list = ArrayList(Arrays.asList(*suggestions))
        addSuggestions(list)
    }

    val historyList: MutableList<SuggestionItem>?
        get() = databaseHelper?.selectAllHistory()

    fun searchQueryList(query:String): MutableList<SuggestionItem>?{
        return databaseHelper?.selectSearchQuery(query)
    }

    private fun refreshAdapterCursor() {
        historyList?.let { adapter?.notifyDataSetChanged(it) }
    }

    @Synchronized
    fun clearSuggestions() {
        mContext.contentResolver.delete(
                HistoryContract.HistoryEntry.CONTENT_URI,
                HistoryContract.HistoryEntry.COLUMN_IS_HISTORY + " = ?", arrayOf("0"))
    }

    @Synchronized
    fun clearHistory() {
        mContext.contentResolver.delete(
                HistoryContract.HistoryEntry.CONTENT_URI,
                HistoryContract.HistoryEntry.COLUMN_IS_HISTORY + " = ?", arrayOf("1"))
    }

    @Synchronized
    fun clearAll() {
        mContext.contentResolver.delete(
                HistoryContract.HistoryEntry.CONTENT_URI,
                null,
                null
        )
    }
    //endregion
    //region Interfaces
    /**
     * Interface that handles the submission and change of search queries.
     */
    interface OnQueryTextListener {
        /**
         * Called when a search query is submitted.
         *
         * @param query The text that will be searched.
         * @return True when the query is handled by the listener, false to let the SearchView handle the default case.
         */
        fun onQueryTextSubmit(query: String?): Boolean

        /**
         * Called when a search query is changed.
         *
         * @param newText The new text of the search query.
         * @return True when the query is handled by the listener, false to let the SearchView handle the default case.
         */
        fun onQueryTextChange(newText: String?): Boolean
    }

    /**
     * Interface that handles the opening and closing of the SearchView.
     */
    interface SearchViewListener {
        /**
         * Called when the searchview is opened.
         */
        fun onSearchViewOpened()

        /**
         * Called when the search view closes.
         */
        fun onSearchViewClosed()
    }

    /**
     * Interface that handles interaction with the voice button.
     */
    interface OnVoiceClickedListener {
        /**
         * Called when the user clicks the voice button.
         */
        fun onVoiceClicked()
    } //endregion

    companion object {
        //region Properties
        /**
         * The freaking log tag. Used for logs, duh.
         */
        private val LOG_TAG = ModooSearchView::class.java.simpleName

        /**
         * The maximum number of results we want to return from the voice recognition.
         */
        private const val MAX_RESULTS = 1

        /**
         * The identifier for the voice request intent. (Guess why it's 42).
         */
        const val REQUEST_VOICE = 42

        /**
         * Number of suggestions to show.
         */
        private var MAX_HISTORY = 10

        /**
         * Sets how many items you want to show from the history database.
         *
         * @param maxHistory - The number of items you want to display.
         */
        fun setMaxHistoryResults(maxHistory: Int) {
            MAX_HISTORY = maxHistory
        }
    }

    //endregion
    //region Constructors
    val databaseHelper by lazy { ModooDatabaseHelper.getInstance(context) }

    init {
        init()
    }
}