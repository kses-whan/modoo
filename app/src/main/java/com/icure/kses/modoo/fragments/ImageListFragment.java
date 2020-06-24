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

package com.icure.kses.modoo.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.icure.kses.modoo.R;
import com.icure.kses.modoo.activity.ModooMainActivity;
import com.icure.kses.modoo.activity.ModooSettingsActivity;
import com.icure.kses.modoo.adapter.SimpleStringRecyclerViewAdapter;
import com.icure.kses.modoo.constant.ModooApiCodes;
import com.icure.kses.modoo.model.ModooItemWrapper;
import com.icure.kses.modoo.model.ModooViewModel;
import com.icure.kses.modoo.vo.ModooItemList;

import java.util.ArrayList;
import java.util.List;


public class ImageListFragment extends Fragment {

    public static final String STRING_IMAGE_URI = "STRING_IMAGE_URI";
    public static final String STRING_ITEM_CODE = "STRING_ITEM_CODE";
    private static ModooMainActivity mActivity;
    private SwipeRefreshLayout mSwipeToRefreshView;
    private RecyclerView rv = null;
    private List<ModooItemList> mItems = null;
    private int category = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        category = ImageListFragment.this.getArguments().getInt("type");

        mActivity = (ModooMainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mSwipeToRefreshView = (SwipeRefreshLayout) inflater.inflate(R.layout.layout_recylerview_list, container, false);
        rv = mSwipeToRefreshView.findViewById(R.id.recyclerview);

        return mSwipeToRefreshView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSwipeToRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateItemList(category);
            }
        });
    }

    SimpleStringRecyclerViewAdapter simpleStringRecyclerViewAdapter = null;

    private void setupRecyclerView(RecyclerView recyclerView, List<ModooItemList> items) {
        if(recyclerView == null){
            // error
            return;
        }

        simpleStringRecyclerViewAdapter = new SimpleStringRecyclerViewAdapter(getActivity(), recyclerView, items);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(simpleStringRecyclerViewAdapter);
    }

    protected void updateItemList(int category){
        moDooViewModel.getItemListData(category).observe(getViewLifecycleOwner(), new Observer<ModooItemWrapper>() {
            @Override
            public void onChanged(ModooItemWrapper modooItemWrapper) {
                if(modooItemWrapper != null){
                    if(!modooItemWrapper.resultCode.equalsIgnoreCase(ModooApiCodes.API_RETURNCODE_SUCCESS)){
                        Toast.makeText(mActivity, "getItemListData Error 1", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    setItems(modooItemWrapper.itemList);
                } else {
                    Toast.makeText(mActivity, "getItemListData Error 2", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    protected ModooViewModel moDooViewModel;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        moDooViewModel = ViewModelProviders.of(this).get(ModooViewModel.class);
        moDooViewModel.getItemListData(category).observe(getViewLifecycleOwner(), new Observer<ModooItemWrapper>() {
            @Override
            public void onChanged(ModooItemWrapper modooItemWrapper) {
                if(modooItemWrapper != null){
                    if(!modooItemWrapper.resultCode.equalsIgnoreCase(ModooApiCodes.API_RETURNCODE_SUCCESS)){
                        return;
                    }
                    setItems(modooItemWrapper.itemList);
                } else {
                    return;
                }
            }
        });

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        prefs.registerOnSharedPreferenceChangeListener(mPrefListener);
    }

    private SharedPreferences.OnSharedPreferenceChangeListener mPrefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if(ModooSettingsActivity.PREF_PRICES.equals(key)){
                List<ModooItemList> items = moDooViewModel.getItemListData(category).getValue().itemList;
                if(items != null)
                    setItems(items);
            }
        }
    };

    public void setItems(List<ModooItemList> items){

        updateFromPreferences();

        if(mItems == null){
            mItems = new ArrayList<ModooItemList>();
        }

        for(ModooItemList item : items){

            if(item.itemPrice >= (long)mMinPrice){
                if(!mItems.contains(item)) {
                    mItems.add(item);
                    if(rv.getAdapter() != null) {
                        simpleStringRecyclerViewAdapter.notifyItemInserted(mItems.indexOf(item));
                    }
                }
            }
        }

        for(int i = mItems.size() - 1 ; i >= 0; i--){
            if(mItems.get(i).itemPrice < (long)mMinPrice){
                mItems.remove(i);
                if(rv.getAdapter() != null) {
                    simpleStringRecyclerViewAdapter.notifyItemRemoved(i);
                }
            }
        }

        if(rv.getAdapter() == null) {
            setupRecyclerView(rv, mItems);
            return;
        }

//        simpleStringRecyclerViewAdapter.mItems = mItems;
//        simpleStringRecyclerViewAdapter.notifyDataSetChanged();

        mSwipeToRefreshView.setRefreshing(false);
    }

    private int mMinPrice = 0;

    private void updateFromPreferences(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        mMinPrice = Integer.parseInt(prefs.getString(ModooSettingsActivity.PREF_PRICES, "0"));

    }
}
