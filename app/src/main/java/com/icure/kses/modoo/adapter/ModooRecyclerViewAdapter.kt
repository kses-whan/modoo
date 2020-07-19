package com.icure.kses.modoo.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.icure.kses.modoo.R
import com.icure.kses.modoo.activity.ModooItemDetailsActivity
import com.icure.kses.modoo.databinding.ListItemBinding
import com.icure.kses.modoo.fragments.HomeListFragment
import com.icure.kses.modoo.utility.ModooDataUtils
import com.icure.kses.modoo.vo.ModooListItem
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class ModooRecyclerViewAdapter(var activity: Activity, var mItems: List<ModooListItem>?) : RecyclerView.Adapter<ModooRecyclerViewAdapter.ViewHolder>() {

    inner class ViewHolder(val listItemBinding: ListItemBinding) : RecyclerView.ViewHolder(listItemBinding.root){
        init {
            listItemBinding.timeformat = TIME_FORMAT
            listItemBinding.priceformat = PRICE_FORMAT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onViewRecycled(holder: ViewHolder) {}
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {

        val modooDataUtils = ModooDataUtils()

        val uriPath = mItems?.get(position)?.thumbUrl
        val uri = Uri.parse(uriPath)

        Glide.with(activity)
                .load(uri)
                .into(holder.listItemBinding.image1)

        holder.listItemBinding.cardview.setOnClickListener {
            val activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(activity
                    , Pair(holder.listItemBinding.image1, ModooItemDetailsActivity.VIEW_NAME_HEADER_IMAGE)
                    , Pair(holder.listItemBinding.tvItemName, ModooItemDetailsActivity.VIEW_NAME_HEADER_NAME)
                    , Pair(holder.listItemBinding.tvItemPrice, ModooItemDetailsActivity.VIEW_NAME_HEADER_PRICE))
            val intent = Intent(activity, ModooItemDetailsActivity::class.java)
            intent.putExtra(HomeListFragment.STRING_IMAGE_URI, mItems?.get(position)?.repImageUrl)
            intent.putExtra(HomeListFragment.STRING_ITEM_CODE, mItems?.get(position)?.itemCode)
            ActivityCompat.startActivity(activity, intent, activityOptions.toBundle())
            activity.overridePendingTransition(R.anim.slide_up, R.anim.scale_down)
        }
        holder.listItemBinding.icWishlist.setOnClickListener {

            var wishMessage = if (ModooDataUtils.wishlist.contains(mItems?.get(position))) {
                modooDataUtils.removeWishlist(mItems?.get(position))
                "Item is deleted from wishlist."
            } else {
                modooDataUtils.addWishlist(mItems?.get(position))
                "Item added to wishlist."
            }
            notifyDataSetChanged()
            Toast.makeText(activity, wishMessage, Toast.LENGTH_SHORT).show()
        }

        if(ModooDataUtils.wishlist.contains(mItems?.get(position))){
            holder.listItemBinding.icWishlist.setImageResource(R.drawable.ic_favorite_black_18dp)
        } else {
            holder.listItemBinding.icWishlist.setImageResource(R.drawable.ic_favorite_border_black_18dp)
        }

        holder.listItemBinding.modooitem = mItems?.get(position)
        holder.listItemBinding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        if(mItems == null) return 0
        return mItems!!.size
    }

    companion object {
        val TIME_FORMAT = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.KOREA)
        val PRICE_FORMAT = NumberFormat.getCurrencyInstance(Locale.KOREA)
    }

    fun notifyDataSetChanged(items: List<ModooListItem>?){
        mItems = items
        notifyDataSetChanged()
    }
}