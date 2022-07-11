package com.albar.computerstore.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.albar.computerstore.ui.fragments.member.ProductComputerStoreFragment

class ComputerStoreProductPagerAdapter(fm: Fragment) : FragmentStateAdapter(fm) {
    private val type = arrayListOf("Computers", "Accessories")

    override fun getItemCount(): Int = type.size

    override fun createFragment(position: Int): Fragment {
        return ProductComputerStoreFragment.newInstance(type[position])
    }
}