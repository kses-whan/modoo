package com.icure.kses.modoo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.icure.kses.modoo.R
import com.icure.kses.modoo.activity.ModooHomeActivity

class SearchFragment : Fragment() {

    var mActivity: ModooHomeActivity? = null
    var menuNum = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        menuNum = this.requireArguments().getInt("menu")
        mActivity = activity as ModooHomeActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        mActivity?.supportActionBar?.setTitle(R.string.menu_item_2)
    }
}