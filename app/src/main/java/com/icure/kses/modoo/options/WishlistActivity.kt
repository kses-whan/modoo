package com.icure.kses.modoo.options

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.icure.kses.modoo.R
import com.icure.kses.modoo.activity.ModooItemDetailsActivity
import com.icure.kses.modoo.fragments.HomeListFragment
import com.icure.kses.modoo.utility.ModooDataUtils
import com.icure.kses.modoo.vo.ModooListItem
import kotlinx.android.synthetic.main.layout_recylerview_list.*
import java.util.*

class WishlistActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.layout_recylerview_list)
        mContext = this

        val recylerViewLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        recyclerview.layoutManager = recylerViewLayoutManager
        recyclerview.adapter = SimpleStringRecyclerViewAdapter(ModooDataUtils.wishlist)
    }

    class SimpleStringRecyclerViewAdapter(private val mWishlist: ArrayList<ModooListItem>) : RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder>() {

        class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
            val mImageView: ImageView
            val mLayoutItem: LinearLayout
            val mImageViewWishlist: ImageView

            init {
                mImageView = mView.findViewById<View>(R.id.image_wishlist) as ImageView
                mLayoutItem = mView.findViewById<View>(R.id.layout_item_desc) as LinearLayout
                mImageViewWishlist = mView.findViewById<View>(R.id.ic_wishlist) as ImageView
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_wishlist_item, parent, false)
            return ViewHolder(view)
        }

        override fun onViewRecycled(holder: ViewHolder) {}

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val uri = Uri.parse(mWishlist[position].thumbUrl)
            Glide.with(mContext!!)
                    .load(uri)
                    .into(holder.mImageView)
            holder.mLayoutItem.setOnClickListener {
                val intent = Intent(mContext, ModooItemDetailsActivity::class.java)
                intent.putExtra(HomeListFragment.STRING_ITEM_CODE, mWishlist[position].itemCode)
                intent.putExtra(HomeListFragment.STRING_IMAGE_URI, mWishlist[position].repImageUrl)
                mContext?.startActivity(intent)
            }

            //Set click action for wishlist
            holder.mImageViewWishlist.setOnClickListener {
                val modooDataUtils = ModooDataUtils()
                modooDataUtils.removeWishlist(position)
                notifyDataSetChanged()
            }
        }

        override fun getItemCount(): Int {
            return mWishlist?.size
        }
    }

    companion object{
        var mContext:Context? = null
    }
}