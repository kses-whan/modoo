package com.icure.kses.modoo.activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.icure.kses.modoo.R
import com.icure.kses.modoo.adapter.ModooRecyclerViewAdapter
import com.icure.kses.modoo.constant.ModooApiCodes
import com.icure.kses.modoo.customview.search.ModooSearchView
import com.icure.kses.modoo.database.ModooDatabaseHelper
import com.icure.kses.modoo.model.ModooViewModel
import com.icure.kses.modoo.utility.PrefManager
import com.icure.kses.modoo.vo.ModooListItem
import kotlinx.android.synthetic.main.activity_search_result.*
import kotlinx.android.synthetic.main.layout_recylerview_list.*
import okhttp3.internal.format
import java.util.ArrayList

class ModooSearchActivity : AppCompatActivity() {

    private val moDooViewModel: ModooViewModel by lazy { ViewModelProviders.of(this@ModooSearchActivity).get(ModooViewModel::class.java) }
    private var modooRecyclerViewAdapter: ModooRecyclerViewAdapter? = null
    private val prefManager: PrefManager by lazy { PrefManager(this@ModooSearchActivity)}
    private val context: Context = this@ModooSearchActivity
    private val databaseHelper = ModooDatabaseHelper(this@ModooSearchActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)

//        databaseHelper.selectAllHistoryTest()

        searchViewInit()
    }

    private fun doSearch(query: String?){
        moDooViewModel?.getItemListData(3)?.observe(this, Observer {
            it?.let {
                if (!it.resultCode.equals(ModooApiCodes.API_RETURNCODE_SUCCESS, ignoreCase = true)) {
                    return@Observer
                }
                it.itemList?.let {
                    setItems(it)
                    tv_search_header_result.setText(format(getString(R.string.layout_search_result), it.size))
                }
            }
        })
    }

    private fun setupRecyclerView(items: MutableList<ModooListItem>?) {
        rv_search_result?.let {
            modooRecyclerViewAdapter = ModooRecyclerViewAdapter(this@ModooSearchActivity, items)
            it.adapter = modooRecyclerViewAdapter
            it.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            it.setHasFixedSize(true)
        }
    }

    fun setItems(items: MutableList<ModooListItem>?) {

//        items?.let { it ->
//            for (i in it.indices.reversed()) {
//                if (it[i].itemPrice < mMinPrice.toLong()) {
//                    it.removeAt(i)
//                }
//            }
//        }

//        mSwipeToRefreshView?.isRefreshing = false

        if (recyclerview?.adapter == null) {
            setupRecyclerView(items)
            return
        } else {
            modooRecyclerViewAdapter?.notifyDataSetChanged(items)
        }
    }

    fun searchViewInit(){
        mdsv_search?.setOnQueryTextListener(object: ModooSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.i("tagg","onQueryTextSubmit : ${query}")
                doSearch(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        mdsv_search?.setSearchViewListener(object : ModooSearchView.SearchViewListener {
            override fun onSearchViewOpened() {
                // Do something once the view is open.
            }

            override fun onSearchViewClosed() {
                // Do something once the view is closed.
            }
        })
        mdsv_search?.run {
            itemClickListener = {view, i ->
                var suggestion = mdsv_search?.getSuggestionAtPosition(i)
                Log.i("tagg","suggestion : ${suggestion}")
                mdsv_search?.setQuery(suggestion, false)
            }
        }
        mdsv_search.adjustTintAlpha(0.8f)

        //test
        val list: MutableList<String> = ArrayList()
        list.add("연관검색1")
        list.add("연관검색2")
        list.add("연관검색3")
        list.add("연관검색4")
        list.add("연관검색5")
        list.add("연관검색6")
        list.add("연관검색7")
        mdsv_search.addSuggestions(list)

        mdsv_search.openSearch()

        tv_search_et_fake.setOnClickListener { mdsv_search.openSearch() }
    }

    override fun onBackPressed() {
        if (mdsv_search.isOpen) {
            // Close the search on the back button press.
            mdsv_search.closeSearch()
            return
        } else {
            super.onBackPressed()
        }
        super.onBackPressed()
    }

    override fun onPause() {
        super.onPause()
//        mdsv_search.clearSuggestions()
    }

    override fun onResume() {
        super.onResume()
        mdsv_search.activityResumed()
    }
}