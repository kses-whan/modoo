package com.icure.kses.modoo.activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
import okhttp3.internal.format
import java.util.*

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
        searchResultHeaderInit()
    }

    private fun doSearch(query: String?){
        moDooViewModel?.getItemListData(3)?.observe(this, Observer {
            Log.i("tagg","getItemListData!! : ${it.resultCode}")
            it?.let {
                if (!it.resultCode.equals(ModooApiCodes.API_RETURNCODE_SUCCESS, ignoreCase = true)) {
                    return@Observer
                }
                it.itemList?.let {
                    setItems(
                        when(sp_search_header_align?.selectedItemPosition){
                            //높은가격순
                            3 -> it.sortedBy { item -> item.itemPrice }?.reversed().toMutableList()
                            //낮은가격순
                            4 -> it.sortedBy { item -> item.itemPrice }.toMutableList()
                            else -> it
                        }
                    )
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



        if (rv_search_result?.adapter == null) {
            Log.i("tagg","11111111111111")
            setupRecyclerView(items)
        } else {
            //기존리스트에 추가
            items?.let {
//                modooRecyclerViewAdapter?.notifyDataSetChanged(items)
                modooRecyclerViewAdapter?.addDataset(items)
            }
        }
        modooRecyclerViewAdapter?.let { tv_search_header_result.setText(format(getString(R.string.layout_search_result), it.itemCount)) }
    }

    fun searchViewInit(){
        mdsv_search?.setOnQueryTextListener(object: ModooSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
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

    fun searchResultHeaderInit(){
        sp_search_header_align.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, resources.getStringArray(R.array.search_filter_options))
        sp_search_header_align.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(position){
                    //인기순
                    0 -> {}
                    //판매순
                    1 -> {}
                    //최신순
                    2 -> {}
                    //높은가격순
                    3 -> {
                        modooRecyclerViewAdapter?.notifyDataSetChanged(modooRecyclerViewAdapter?.mItems?.sortedBy { item -> item.itemPrice }?.reversed())
                    }
                    //낮은가격순
                    4 -> {
                        modooRecyclerViewAdapter?.notifyDataSetChanged(modooRecyclerViewAdapter?.mItems?.sortedBy { item -> item.itemPrice })
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
        sp_search_header_align.setSelection(0)
    }
}