package com.icure.kses.modoo.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.icure.kses.modoo.R
import com.icure.kses.modoo.adapter.PickViewPagerAdapter
import com.icure.kses.modoo.adapter.TodayPickAdapter
import com.icure.kses.modoo.constant.ModooApiCodes
import com.icure.kses.modoo.customview.cardstack.*
import com.icure.kses.modoo.model.ModooViewModel
import com.icure.kses.modoo.vo.ModooListItem
import com.skydoves.expandablelayout.OnExpandListener
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.search_bar.*

class ModooHomeActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener, CardStackListener {

    private val todayPickManager by lazy { CardStackLayoutManager(this, this) }
    private val moDooViewModel: ModooViewModel by lazy { ViewModelProviders.of(this).get(ModooViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        bnv_view.setOnNavigationItemSelectedListener(this)

        mnsv_home_pick.run {
            header = tv_home_sticky
            stickListener = {v, i -> Log.i("tagg","stick!!!!${v.getId()} , ${i}")}
            freeListener = {Log.i("tagg","free!!!!")}
        }

        expl_home.onExpandListener = object : OnExpandListener{
            override fun onExpand(isExpanded: Boolean) {}
        }

        expl_home.parentLayout.setOnClickListener {
            if(expl_home.isExpanded)
                expl_home.collapse()
            else
                expl_home.expand()
        }

        vp_home_pick?.adapter = PickViewPagerAdapter(this@ModooHomeActivity)
        di_home_pick.attachViewPager(vp_home_pick)

        tv_searchbar_text.setOnClickListener { startActivity(Intent(this, ModooSearchActivity::class.java)) }

        moDooViewModel?.getItemListData(2)?.observe(this, Observer {
            it?.let {
                if (!it.resultCode.equals(ModooApiCodes.API_RETURNCODE_SUCCESS, ignoreCase = true)) {
                    return@Observer
                }
                it.itemList?.let {
                    setupCardView(it)
                }
            }
        })
    }

    fun setupCardView(itemList:MutableList<ModooListItem>){

        if(itemList.size <= 0){
            return
        }

        todayPickManager.setStackFrom(StackFrom.Right)
        todayPickManager.setVisibleCount(3)
        todayPickManager.setTranslationInterval(20.0f)
        todayPickManager.setScaleInterval(0.90f)
        todayPickManager.setSwipeThreshold(0.3f)
        todayPickManager.setMaxDegree(0f)
        todayPickManager.setDirections(Direction.HORIZONTAL)
        todayPickManager.setCanScrollHorizontal(true)
        todayPickManager.setCanScrollVertical(false)
        todayPickManager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
        todayPickManager.setOverlayInterpolator(LinearInterpolator())
        todayPickManager.setMaxIndex(itemList.size - 1)

        ss_home_today.layoutManager = todayPickManager
        var adapter = TodayPickAdapter(this, itemList)
        ss_home_today.adapter = adapter
        ss_home_today.itemAnimator.apply {
            if(this is DefaultItemAnimator){
                supportsChangeAnimations = false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        bnv_view.menu.getItem(2).setChecked(true)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    companion object {
        var notificationCountCart = 0
        var notificationPushMsg = 0
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
    }

    override fun onNavigationItemSelected(menu: MenuItem): Boolean {

        Log.i("tagg","onNavigationItemSelected : " + menu)

        when(menu.itemId){
            R.id.nav_item2 -> startActivity(Intent(this, ModooSearchActivity::class.java))
        }
        return true
    }

    override fun onCardDisappeared(view: View?, position: Int) {
//        Log.i("tagg","onCardDisappeared : ${position}")
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {
//        Log.i("tagg","onCardDragging : ${direction} , ratio : ${ratio}")
    }

    override fun onCardSwiped(direction: Direction?) {
//        Log.i("tagg","onCardSwiped : ${direction}")
    }

    override fun onCardCanceled() {
//        Log.i("tagg","onCardCanceled")
    }

    override fun onCardAppeared(view: View?, position: Int) {

    }

    override fun onCardRewound() {

    }
}