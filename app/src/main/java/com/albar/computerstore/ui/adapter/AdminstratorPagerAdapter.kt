package com.albar.computerstore.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.albar.computerstore.ui.fragments.admin.UnverifiedFragment
import com.albar.computerstore.ui.fragments.admin.VerifiedFragment

class AdministratorPagerAdapter(fm: Fragment) : FragmentStateAdapter(fm) {

    private val totalFragment = arrayListOf(UnverifiedFragment(), VerifiedFragment())

    override fun getItemCount(): Int = totalFragment.size

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null

        when (position) {
            0 -> fragment = totalFragment[position]
            1 -> fragment = totalFragment[position]
        }

        return fragment as Fragment
    }


}