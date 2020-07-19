package com.icure.kses.modoo.options

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.icure.kses.modoo.R
import com.icure.kses.modoo.activity.ModooItemDetailsActivity
import com.icure.kses.modoo.activity.ModooHomeActivity
import com.icure.kses.modoo.fragments.HomeListFragment
import com.icure.kses.modoo.utility.ModooDataUtils
import com.icure.kses.modoo.vo.ModooListItem
import kotlinx.android.synthetic.main.activity_cart_list.*
import java.util.*

class CartListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart_list)

        mContext = this@CartListActivity

        //Show cart layout based on items
        setCartLayout()

        val recylerViewLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(mContext)
        recyclerview.layoutManager = recylerViewLayoutManager
        recyclerview.adapter = SimpleStringRecyclerViewAdapter(recyclerview, ModooDataUtils.cartList)

        val payment = findViewById<Button>(R.id.btn_action_payment)
        payment.setOnClickListener {
            AlertDialog.Builder(this@CartListActivity)
                    .setMessage(R.string.confirm_payment)
                    .setPositiveButton(R.string.dialog_ok) { dialog, which -> dialog.dismiss() }
                    .setCancelable(true)
                    .create()
                    .show()
        }
    }

    class SimpleStringRecyclerViewAdapter(private val mRecyclerView: RecyclerView, private val mCartlist: ArrayList<ModooListItem>?) : RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder>() {

        class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
            val mImageView: ImageView
            val mLayoutItem: LinearLayout
            val mLayoutRemove: LinearLayout
            val mLayoutEdit: LinearLayout

            init {
                mImageView = mView.findViewById<View>(R.id.image_cartlist) as ImageView
                mLayoutItem = mView.findViewById<View>(R.id.layout_item_desc) as LinearLayout
                mLayoutRemove = mView.findViewById<View>(R.id.layout_action1) as LinearLayout
                mLayoutEdit = mView.findViewById<View>(R.id.layout_action2) as LinearLayout
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_cartlist_item, parent, false)
            return ViewHolder(view)
        }

        override fun onViewRecycled(holder: ViewHolder) {}

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val uri = Uri.parse(mCartlist!![position].thumbUrl)
            Glide.with(mContext!!)
                    .load(uri)
                    .into(holder.mImageView)
            holder.mLayoutItem.setOnClickListener {
                val intent = Intent(mContext, ModooItemDetailsActivity::class.java)
                intent.putExtra(HomeListFragment.STRING_ITEM_CODE, mCartlist[position].itemCode)
                intent.putExtra(HomeListFragment.STRING_IMAGE_URI, mCartlist[position].repImageUrl)
                mContext!!.startActivity(intent)
            }

            //Set click action
            holder.mLayoutRemove.setOnClickListener {
                val modooDataUtils = ModooDataUtils()
                modooDataUtils.removeCartList(position)
                notifyDataSetChanged()
                //Decrease notification count
                ModooHomeActivity.notificationCountCart--
            }

            //Set click action
            holder.mLayoutEdit.setOnClickListener { }
        }

        override fun getItemCount(): Int {
            return mCartlist?.size ?: 0
        }

    }

    protected fun setCartLayout() {
        if (ModooHomeActivity.notificationCountCart > 0) {
            layout_cart_empty.visibility = View.GONE
            layout_items.visibility = View.VISIBLE
            layout_payment.visibility = View.VISIBLE
        } else {
            layout_cart_empty.visibility = View.VISIBLE
            layout_items.visibility = View.GONE
            layout_payment.visibility = View.GONE
            val bStartShopping = findViewById<View>(R.id.bAddNew) as Button
            bStartShopping.setOnClickListener { finish() }
        }
    }

    companion object {
        private var mContext: Context? = null
    }
}