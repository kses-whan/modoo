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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
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
import com.icure.kses.modoo.constant.Modoo_Api_Codes;
import com.icure.kses.modoo.databinding.ListItemBinding;
import com.icure.kses.modoo.vo.ModooItemList;
import com.icure.kses.modoo.model.ModooItemWrapper;
import com.icure.kses.modoo.model.ModooViewModel;
import com.icure.kses.modoo.product.ItemDetailsActivity;
import com.icure.kses.modoo.utility.ModooDataUtils;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


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

        simpleStringRecyclerViewAdapter = new SimpleStringRecyclerViewAdapter(recyclerView, items);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(simpleStringRecyclerViewAdapter);
    }

    public static class SimpleStringRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {

        private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.KOREA);
        private static final NumberFormat PRICE_FORMAT = NumberFormat.getCurrencyInstance(Locale.KOREA);

        public List<ModooItemList> mItems;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public final ListItemBinding listItemBinding;

            public ViewHolder(ListItemBinding listItemBinding) {
                super(listItemBinding.getRoot());
                this.listItemBinding = listItemBinding;
                listItemBinding.setTimeformat(TIME_FORMAT);
                listItemBinding.setPriceformat(PRICE_FORMAT);
            }
        }

        public SimpleStringRecyclerViewAdapter(RecyclerView recyclerView, List<ModooItemList> items) {
            this.mItems = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ListItemBinding binding = ListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onViewRecycled(ViewHolder holder) {
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

           if(mItems == null){
               return;
           }

            final Uri uri = Uri.parse(mItems.get(position).thumbUrl);

            Picasso.with(mActivity)
                    .load(uri)
                    .into(holder.listItemBinding.image1);

            holder.listItemBinding.cardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity
                            , new Pair<View, String>(holder.listItemBinding.image1, ItemDetailsActivity.VIEW_NAME_HEADER_IMAGE)
                            , new Pair<View, String>(holder.listItemBinding.tvItemName, ItemDetailsActivity.VIEW_NAME_HEADER_NAME)
                            , new Pair<View, String>(holder.listItemBinding.tvItemPrice, ItemDetailsActivity.VIEW_NAME_HEADER_PRICE));
                    Intent intent = new Intent(mActivity, ItemDetailsActivity.class);
                    intent.putExtra(STRING_IMAGE_URI, mItems.get(position).repImageUrl);
                    intent.putExtra(STRING_ITEM_CODE, mItems.get(position).itemCode);
                    ActivityCompat.startActivity(mActivity, intent, activityOptions.toBundle());
                }
            });

            //Set click action for wishlist
            holder.listItemBinding.icWishlist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String wishMessage = "";

                    ModooDataUtils modooDataUtils = new ModooDataUtils();
                    if(modooDataUtils.getWishlist().contains(mItems.get(position).itemCode)){
                        modooDataUtils.removeWishlist(mItems.get(position));
                        wishMessage = "Item is deleted from wishlist.";
                    } else {
                        modooDataUtils.addWishlist(mItems.get(position));
                        wishMessage = "Item added to wishlist.";
                    }

                    notifyDataSetChanged();
                    Toast.makeText(mActivity,wishMessage,Toast.LENGTH_SHORT).show();
                }
            });

            ModooDataUtils modooDataUtils = new ModooDataUtils();
            if(modooDataUtils.getWishlist().contains(mItems.get(position))){
                holder.listItemBinding.icWishlist.setImageResource(R.drawable.ic_favorite_black_18dp);
            } else {
                holder.listItemBinding.icWishlist.setImageResource(R.drawable.ic_favorite_border_black_18dp);
            }

            holder.listItemBinding.setModooitem(mItems.get(position));
            holder.listItemBinding.executePendingBindings();
        }

        @Override
        public int getItemCount() {
            if(mItems == null){
                return 0;
            }
            return mItems.size();
        }
    }

    protected void updateItemList(int category){
        moDooViewModel.getItemListData(category).observe(getViewLifecycleOwner(), new Observer<ModooItemWrapper>() {
            @Override
            public void onChanged(ModooItemWrapper modooItemWrapper) {
                if(modooItemWrapper != null){
                    if(!modooItemWrapper.resultCode.equalsIgnoreCase(Modoo_Api_Codes.API_RETURNCODE_SUCCESS)){
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
                    if(!modooItemWrapper.resultCode.equalsIgnoreCase(Modoo_Api_Codes.API_RETURNCODE_SUCCESS)){
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

        Log.i("tagg","setItems : " + items.size());

        updateFromPreferences();

        if(mItems == null){
            mItems = new ArrayList<ModooItemList>();
        }

        for(ModooItemList item : items){

            Log.i("tagg","item.itemPrice : " + item.itemPrice);

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

        Log.i("tagg","mMinPrice : " + mMinPrice);
    }
}
