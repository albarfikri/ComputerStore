package com.albar.computerstore.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.albar.computerstore.ui.fragments.detail.ComputerFragment
import com.albar.computerstore.ui.fragments.detail.OverviewFragment

class DetailSectionsPagerAdapter(fm: Fragment) : FragmentStateAdapter(fm) {

    private val totalFragment = arrayListOf(OverviewFragment(), ComputerFragment())

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = totalFragment[position]
            1 -> fragment = totalFragment[position]
        }
        return fragment as Fragment
    }

    override fun getItemCount(): Int = totalFragment.size

}