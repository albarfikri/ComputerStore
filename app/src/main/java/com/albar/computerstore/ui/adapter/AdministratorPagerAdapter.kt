package com.albar.computerstore.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.albar.computerstore.ui.fragments.admin.UnverifiedAndVerifiedFragment

class AdministratorPagerAdapter(fm: Fragment) : FragmentStateAdapter(fm) {
    private val isVerified = arrayListOf(false, true)

    override fun getItemCount(): Int = isVerified.size

    override fun createFragment(position: Int): Fragment {
        return UnverifiedAndVerifiedFragment.newInstance(isVerified[position])
    }
}