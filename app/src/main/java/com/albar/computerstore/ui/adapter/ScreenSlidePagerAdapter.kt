package com.albar.computerstore.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.albar.computerstore.others.Constants.NUM_PAGES
import com.albar.computerstore.ui.fragments.opening.OnBoardingFragment1
import com.albar.computerstore.ui.fragments.opening.OnBoardingFragment2
import com.albar.computerstore.ui.fragments.opening.OnBoardingFragment3

class ScreenSlidePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(
    fm,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {

    override fun getCount(): Int = NUM_PAGES

    override fun getItem(position: Int): Fragment =
        when (position) {
            0 -> OnBoardingFragment1()
            1 -> OnBoardingFragment2()
            2 -> OnBoardingFragment3()
            else -> Fragment()
        }
}