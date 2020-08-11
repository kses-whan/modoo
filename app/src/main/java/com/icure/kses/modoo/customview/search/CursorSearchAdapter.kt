package com.icure.kses.modoo.customview.search

import android.content.Context
import android.database.Cursor
import android.os.Build
import android.text.Html
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.icure.kses.modoo.R
import com.icure.kses.modoo.database.ModooDatabaseHelper
import com.icure.kses.modoo.vo.SuggestionItem

/**
 * Created by mauker on 19/04/2016.
 *
 * Default adapter used for the suggestion/history ListView.
 */
class CursorSearchAdapter(
        val context: Context,
        var items: MutableList<SuggestionItem>?,
        val modooSearchView: ModooSearchView
) : RecyclerView.Adapter<CursorSearchAdapter.ListViewHolder>() {

//    init {
//        val databaseHelper = ModooDatabaseHelper.getInstance(context)
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return ListViewHolder(LayoutInflater.from(context).inflate(R.layout.suggestion_list_item, parent, false) as CardView)
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var item = items?.get(position)

        with(holder){
            bind(item)
        }

        val isHistory = item?.isHistory ?: false
        val iconRes = if (isHistory) holder.historyIcon else holder.suggestionIcon
        holder.iv_remove.visibility = if(isHistory) View.VISIBLE else View.GONE
        holder.iv_icon.setImageResource(iconRes)
        holder.iv_remove.setImageResource(holder.removeIcon)
    }

    var lastQuery = ""

    fun getItem(position: Int): Any {
        return items?.get(position)!!.text
    }

    fun setLastFilterQuery(query:String) {
        this.lastQuery = query
    }

    inner class ListViewHolder(val cardView: CardView): RecyclerView.ViewHolder(cardView), View.OnClickListener {

        var historyIcon = R.drawable.ic_history_white
        var suggestionIcon = R.drawable.ic_action_search_white
        var removeIcon = R.drawable.ic_action_navigation_close

        val iv_icon: ImageView
        val iv_remove: ImageView
        val tv_content: TextView

        init{
            iv_icon = cardView.findViewById(R.id.iv_icon)
            iv_remove = cardView.findViewById(R.id.iv_remove_icon)
            tv_content = cardView.findViewById(R.id.tv_str)
            cardView.setOnClickListener(this)
            iv_remove.setOnClickListener(this)
        }

        @RequiresApi(Build.VERSION_CODES.N)
        fun bind(item:SuggestionItem?){
            val changedText = item?.text?.replace(lastQuery, "<font color=#FFB400><b>${lastQuery}</b></font>")
            tv_content.setText(Html.fromHtml(changedText, Html.FROM_HTML_MODE_LEGACY))
        }

        override fun onClick(v: View?) {
            when(v?.id){
                R.id.cv_search_suggestion -> {
                    modooSearchView.itemClickListener(cardView, adapterPosition)
                }
                R.id.iv_remove_icon -> {
                    Log.i("tagg","onClick : iv_remove_icon")
                    layoutPosition.also { currentPosition ->
                        Log.i("tagg","onClick : currentPosition : ${currentPosition}")
                        val text = tv_content.text.toString()
                        if (!TextUtils.isEmpty(text)) {
                            modooSearchView.removeSuggestion(text)
                        }
                        var results =
                            if(TextUtils.isEmpty(lastQuery.trim())){
                                Log.i("tagg","11111")
                                modooSearchView.historyList
                            } else {
                                Log.i("tagg","222222")
                                modooSearchView.searchQueryList(lastQuery)
                            }
                        results?.let {
                            items = it
                            Log.i("tagg","result : ${it}")
                        }
                        notifyItemRemoved(currentPosition)
                    }
                }
            }
        }
    }

    fun notifyDataSetChanged(list: MutableList<SuggestionItem>){
        this.items = list
        notifyDataSetChanged()
    }
}
