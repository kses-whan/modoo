package com.icure.kses.modoo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.icure.kses.modoo.R

class PickViewPagerAdapter(_context: Context) : PagerAdapter() {

    private val layouts: IntArray = intArrayOf(
        R.layout.welcome_slide1,
        R.layout.welcome_slide2,
        R.layout.welcome_slide3,
        R.layout.welcome_slide4
    )

    private var layoutInflater: LayoutInflater? = null
    private var context = _context

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
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