package com.albar.computerstore.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.albar.computerstore.data.remote.entity.ComputerStore
import com.albar.computerstore.ui.fragments.member.ProductComputerStoreFragment

class ComputerStoreProductPagerAdapter(fm: Fragment) : FragmentStateAdapter(fm) {
    var objectComputerStore: ComputerStore? = null

    var adapterSetFrom = arrayListOf("Main", "Member")

    var chosenAdapterSetFrom = ""

    private val type = arrayListOf("Computer", "Accessory")

    override fun getItemCount(): Int = type.size

    override fun createFragment(position: Int): Fragment {
        return ProductComputerStoreFragment.newInstance(
            chosenAdapterSetFrom, type[position], objectComputerStore ?: ComputerStore()

        )
    }
}