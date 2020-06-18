package com.icure.kses.modoo.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.icure.kses.modoo.R;
import com.icure.kses.modoo.activity.ItemDetailsActivity;
import com.icure.kses.modoo.databinding.ListItemBinding;
import com.icure.kses.modoo.fragments.ImageListFragment;
import com.icure.kses.modoo.utility.ModooDataUtils;
import com.icure.kses.modoo.vo.ModooItemList;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class SimpleStringRecyclerViewAdapter
        extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {

    static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.KOREA);
    static final NumberFormat PRICE_FORMAT = NumberFormat.getCurrencyInstance(Locale.KOREA);

    public Activity activity;
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

    public SimpleStringRecyclerViewAdapter(Activity activity, RecyclerView recyclerView, List<ModooItemList> items) {
        this.activity = activity;
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
        Picasso.with(activity)
                .load(uri)
                .into(holder.listItemBinding.image1);

        holder.listItemBinding.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(activity
                        , new Pair<View, String>(holder.listItemBinding.image1, ItemDetailsActivity.VIEW_NAME_HEADER_IMAGE)
                        , new Pair<View, String>(holder.listItemBinding.tvItemName, ItemDetailsActivity.VIEW_NAME_HEADER_NAME)
                        , new Pair<View, String>(holder.listItemBinding.tvItemPrice, ItemDetailsActivity.VIEW_NAME_HEADER_PRICE));
                Intent intent = new Intent(activity, ItemDetailsActivity.class);
                intent.putExtra(ImageListFragment.STRING_IMAGE_URI, mItems.get(position).repImageUrl);
                intent.putExtra(ImageListFragment.STRING_ITEM_CODE, mItems.get(position).itemCode);
                ActivityCompat.startActivity(activity, intent, activityOptions.toBundle());
            }
        });

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
                Toast.makeText(activity, wishMessage, Toast.LENGTH_SHORT).show();
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
