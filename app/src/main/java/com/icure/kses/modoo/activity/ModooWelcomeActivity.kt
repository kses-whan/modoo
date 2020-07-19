package com.icure.kses.modoo.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.icure.kses.modoo.R
import com.icure.kses.modoo.adapter.ModooRecyclerViewAdapter
import com.icure.kses.modoo.databinding.ListItemBinding
import com.icure.kses.modoo.utility.PrefManager
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_welcome.*

class ModooWelcomeActivity : AppCompatActivity() {

    private lateinit var layouts: IntArray
    private var prefManager: PrefManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefManager = PrefManager(this)
//        if (!prefManager!!.isFirstTimeLaunch) {
//            launchHomeScreen()
//            finish()
//        }

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }

        setContentView(R.layout.activity_welcome)

        layouts = intArrayOf(
                R.layout.welcome_slide1,
                R.layout.welcome_slide2,
                R.layout.welcome_slide3,
                R.layout.welcome_slide4
            )

        // making notification bar transparent
        changeStatusBarColor()
        vp_welcome?.adapter = WelcomeViewPagerAdapter()
        vp_welcome?.addOnPageChangeListener(viewPagerPageChangeListener)

        di_welcome.attachViewPager(vp_welcome)

        btn_skip?.setOnClickListener { launchHomeScreen() }
        btn_next?.setOnClickListener {
            val current = getItem(+1)
            if (current < layouts.size) {
                vp_welcome?.currentItem = current
            } else {
                launchHomeScreen()
            }
        }


//        ss_home_today?.adapter =
    }

    private fun getItem(i: Int): Int {
        return vp_welcome!!.currentItem + i
    }

    private fun launchHomeScreen() {
        prefManager?.isFirstTimeLaunch = false
        startActivity(Intent(this@ModooWelcomeActivity, ModooLoginActivity::class.java))
        finish()
    }

    //	viewpager change listener
    var viewPagerPageChangeListener: OnPageChangeListener = object : OnPageChangeListener {
        override fun onPageSelected(position: Int) {
            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.size - 1) {
                // last page. make button text to GOT IT
                btn_next?.text = getString(R.string.start)
                btn_skip?.visibility = View.GONE
            } else {
                // still pages are left
                btn_next?.text = getString(R.string.next)
                btn_skip?.visibility = View.VISIBLE
            }
        }

        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
        override fun onPageScrollStateChanged(arg0: Int) {}
    }

    private fun changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    inner class WelcomeViewPagerAdapter : PagerAdapter() {

        private var layoutInflater: LayoutInflater? = null

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = layoutInflater!!.inflate(layouts[position], container, false)
            container.addView(view)
            return view
        }

        override fun getCount(): Int {
            return layouts.size
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view === obj
        }

        override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
            val view = any as View
            container.removeView(view)
        }
    }


}