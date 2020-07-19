package com.icure.kses.modoo.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.transition.Transition
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.google.firebase.analytics.FirebaseAnalytics
import com.icure.kses.modoo.R
import com.icure.kses.modoo.constant.ModooApiCodes
import com.icure.kses.modoo.constant.ModooConstants
import com.icure.kses.modoo.fragments.HomeListFragment
import com.icure.kses.modoo.model.ModooViewModel
import com.icure.kses.modoo.notification.NotificationCountSetClass
import com.icure.kses.modoo.options.CartListActivity
import com.icure.kses.modoo.utility.ModooDataUtils
import com.icure.kses.modoo.vo.ModooItemDetail
import com.icure.kses.modoo.vo.ModooListItem
import kotlinx.android.synthetic.main.activity_item_details.*

class ModooItemDetailsActivity : AppCompatActivity() {
    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    var stringImageUri: String? = null
    var stringItemCode: String? = null
    private var moDooViewModel: ModooViewModel? = null

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_details)
        initFirebaseAnalytics()
        transAnimate()
        handleIntent()
        handleHttpData()
        loadImage()
    }

    private fun initFirebaseAnalytics(){
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun transAnimate() {
        ViewCompat.setTransitionName(image1, VIEW_NAME_HEADER_IMAGE)
        ViewCompat.setTransitionName(tv_detail_name, VIEW_NAME_HEADER_NAME)
        ViewCompat.setTransitionName(tv_detail_price, VIEW_NAME_HEADER_PRICE)
        addTransitionListener()
    }

    private fun handleIntent() {
        intent.let {
            stringImageUri = intent.getStringExtra(HomeListFragment.STRING_IMAGE_URI)
            stringItemCode = intent.getStringExtra(HomeListFragment.STRING_ITEM_CODE)
        }
    }

    private fun handleHttpData() {
        moDooViewModel = ViewModelProviders.of(this@ModooItemDetailsActivity).get(ModooViewModel::class.java)
        moDooViewModel?.getItemDetailData(stringItemCode)?.observe(this@ModooItemDetailsActivity, Observer { modooItemWrapper ->
            modooItemWrapper?.let {
                if (!modooItemWrapper.resultCode.equals(ModooApiCodes.API_RETURNCODE_SUCCESS, ignoreCase = true)) {
                    Toast.makeText(this@ModooItemDetailsActivity, "getItemDetailData Error 1", Toast.LENGTH_SHORT).show()
                    return@Observer
                }
                setDetail(modooItemWrapper.itemDetail)
            }
        })
    }

    private fun setDetail(modooItemDetail: ModooItemDetail?) {
        if (modooItemDetail == null) {
            Log.i("tagg", "ModooItemDetail is null")
            return
        }
        image1.setOnClickListener {
            val intent = Intent(this@ModooItemDetailsActivity, ViewPagerActivity::class.java)
            intent.putStringArrayListExtra(DETAIL_IMAGE_LIST, modooItemDetail.detailImageUrls)
            startActivity(intent)
            overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out)
        }
        text_action_bottom1.setOnClickListener {
            ModooDataUtils().addCartList(createModooListItem(modooItemDetail))
            Toast.makeText(this@ModooItemDetailsActivity, "Item added to cart.", Toast.LENGTH_SHORT).show()
            NotificationCountSetClass.setNotifyCount(++ModooHomeActivity.notificationCountCart)

            //analytics event
            val bundle = Bundle()
            bundle.putString("btn_name", ModooConstants.EVENT_ID_ITEM_DETAILS_ACTIVITY_1)
            bundle.putString("btn_desc", ModooConstants.EVENT_NAME_CART_BTN_ITEM_DETAILS_ACTIVITY)
            mFirebaseAnalytics!!.logEvent("btn_click", bundle)
        }
        text_action_bottom2.setOnClickListener {
            ModooDataUtils().addCartList(createModooListItem(modooItemDetail))
            NotificationCountSetClass.setNotifyCount(++ModooHomeActivity.notificationCountCart)
            startActivity(Intent(this@ModooItemDetailsActivity, CartListActivity::class.java))
        }
    }

    private fun createModooListItem(modooItemDetail: ModooItemDetail): ModooListItem {
        return ModooListItem(
                modooItemDetail.itemCode,
                modooItemDetail.thumbUrl,
                modooItemDetail.repImageUrl,
                modooItemDetail.itemName,
                modooItemDetail.itemPrice,
                modooItemDetail.itemCreateDate
        )
    }

    private fun loadImage() {
        Glide.with(image1.context)
                .load(stringImageUri)
                .into(image1)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun addTransitionListener(): Boolean {
        val transition = window.sharedElementEnterTransition
        if (transition != null) {
            // There is an entering shared element transition so add a listener to it
            transition.addListener(object : Transition.TransitionListener {
                override fun onTransitionEnd(transition: Transition) {
                    Handler().postDelayed({ loadImage() }, 500)
                    transition.removeListener(this)
                }

                override fun onTransitionStart(transition: Transition) {}
                override fun onTransitionCancel(transition: Transition) {
                    transition.removeListener(this)
                }

                override fun onTransitionPause(transition: Transition) {}
                override fun onTransitionResume(transition: Transition) {}
            })
            return true
        }
        return false
    }

    companion object {
        const val VIEW_NAME_HEADER_IMAGE = "detail:header:image"
        const val VIEW_NAME_HEADER_NAME = "detail:header:name"
        const val VIEW_NAME_HEADER_PRICE = "detail:header:price"
        const val DETAIL_IMAGE_LIST = "DETAIL_IMAGE_LIST"
    }
}