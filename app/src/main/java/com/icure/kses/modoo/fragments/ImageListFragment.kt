/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.icure.kses.modoo.fragments

import android.content.Context
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.icure.kses.modoo.R
import com.icure.kses.modoo.activity.ModooMainActivity
import com.icure.kses.modoo.activity.ModooSettingsActivity
import com.icure.kses.modoo.adapter.SimpleStringRecyclerViewAdapter
import com.icure.kses.modoo.constant.ModooApiCodes
import com.icure.kses.modoo.model.ModooViewModel
import com.icure.kses.modoo.vo.ModooListItem
import kotlinx.android.synthetic.main.layout_recylerview_list.*

class ImageListFragment : Fragment() {
    var mSwipeToRefreshView: SwipeRefreshLayout? = null
    var category = 0
    var moDooViewModel: ModooViewModel? = null
    var simpleStringRecyclerViewAdapter: SimpleStringRecyclerViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        category = this.requireArguments().getInt("type")
        mActivity = activity as ModooMainActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mSwipeToRefreshView = inflater.inflate(R.layout.layout_recylerview_list, container, false) as SwipeRefreshLayout
        return mSwipeToRefreshView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mSwipeToRefreshView?.setOnRefreshListener { updateItemList(category) }
    }

    private fun setupRecyclerView(items: List<ModooListItem>?) {
        recyclerview?.let {
            Log.i("tagg","55555555555")
            simpleStringRecyclerViewAdapter = SimpleStringRecyclerViewAdapter(mActivity as ModooMainActivity, items)
            it.adapter = simpleStringRecyclerViewAdapter
            it.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            it.setHasFixedSize(true)
        }
    }

    protected fun updateItemList(category: Int) {
        moDooViewModel?.getItemListData(category)?.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (!it.resultCode.equals(ModooApiCodes.API_RETURNCODE_SUCCESS, ignoreCase = true)) {
                    Toast.makeText(mActivity, "getItemListData Error 1", Toast.LENGTH_SHORT).show()
                    return@Observer
                }
                setItems(it.itemList)
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        moDooViewModel = ViewModelProviders.of(this).get(ModooViewModel::class.java)
        moDooViewModel?.getItemListData(category)?.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (!it.resultCode.equals(ModooApiCodes.API_RETURNCODE_SUCCESS, ignoreCase = true)) {
                    return@Observer
                }
                Log.i("tagg","33333333");
                setItems(it.itemList)
            }
        })
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs.registerOnSharedPreferenceChangeListener(mPrefListener)
    }

    private val mPrefListener = OnSharedPreferenceChangeListener { sharedPreferences, key ->
        if (ModooSettingsActivity.PREF_PRICES == key) {
            val items = moDooViewModel?.getItemListData(category)?.value?.itemList
            items?.let { setItems(it) }
        }
    }

    fun setItems(items: MutableList<ModooListItem>?) {
        Log.i("tagg","setItems!!!")
        updateFromPreferences()

        items?.let { it ->
            for (i in it.indices.reversed()) {
                if (it[i].itemPrice < mMinPrice.toLong()) {
                    it.removeAt(i)
                }
            }
        }

        mSwipeToRefreshView?.isRefreshing = false

        if (recyclerview?.adapter == null) {
            setupRecyclerView(items)
            return
        } else {
            simpleStringRecyclerViewAdapter?.notifyDataSetChanged(items)
        }
    }

    private var mMinPrice = 0
    private fun updateFromPreferences() {
        mMinPrice = PreferenceManager.getDefaultSharedPreferences(context).getString(ModooSettingsActivity.PREF_PRICES, "0")!!.toInt()
    }

    companion object {
        const val STRING_IMAGE_URI = "STRING_IMAGE_URI"
        const val STRING_ITEM_CODE = "STRING_ITEM_CODE"
        private var mActivity: ModooMainActivity? = null
    }
}