package com.icure.kses.modoo.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.icure.kses.modoo.R
import com.icure.kses.modoo.databinding.ListItemBinding
import com.icure.kses.modoo.miscellaneous.BlurTransform
import com.icure.kses.modoo.vo.ModooListItem

class TodayPickAdapter(private val context: Context, private val todayDataset:MutableList<ModooListItem>): RecyclerView.Adapter<TodayPickAdapter.TodayViewHolder>() {

    class TodayViewHolder(cardView: CardView) : RecyclerView.ViewHolder(cardView), View.OnClickListener{
        val mBlurImageView: ImageView
        val mRealImageView: ImageView
        val mTextView: TextView
        val mainView: CardView
        init {
            mBlurImageView = cardView.findViewById<View>(R.id.center_overlay) as ImageView
            mRealImageView = cardView.findViewById<View>(R.id.iv_home_today_bg) as ImageView
            mTextView = cardView.findViewById<View>(R.id.tv_home_today_name) as TextView
            mainView = cardView

            mainView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            Log.i("tagg","mainView clicked!! : " + adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodayViewHolder {
        var mainView = LayoutInflater.from(parent.context).inflate(R.layout.today_item, parent, false) as CardView
        return TodayViewHolder(mainView)
    }

    override fun getItemCount(): Int {
        return todayDataset.size
    }

    override fun onBindViewHolder(holder: TodayViewHolder, position: Int) {
        var url = todayDataset.get(position).repImageUrl
        Glide.with(context)
                .load(url)
                .transform(BlurTransform(context))
//                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.mBlurImageView)
        Glide.with(context)
                .load(url)
//                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.mRealImageView)
        holder.mTextView.setText(todayDataset.get(position).itemName)
    }
}