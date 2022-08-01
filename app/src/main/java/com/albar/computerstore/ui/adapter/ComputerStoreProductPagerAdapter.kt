package com.albar.computerstore.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.albar.computerstore.data.remote.entity.ComputerStore
import com.albar.computerstore.others.Constants.ACCESSORY
import com.albar.computerstore.others.Constants.ADMIN
import com.albar.computerstore.others.Constants.COMPUTER
import com.albar.computerstore.others.Constants.MAIN
import com.albar.computerstore.others.Constants.MEMBER
import com.albar.computerstore.ui.fragments.member.ProductComputerStoreFragment

class ComputerStoreProductPagerAdapter(fm: Fragment) : FragmentStateAdapter(fm) {
    var objectComputerStore: ComputerStore? = null

    var adapterSetFrom = arrayListOf(MAIN, MEMBER, ADMIN)

    var chosenAdapterSetFrom = ""

    private val type = arrayListOf(COMPUTER, ACCESSORY)

    override fun getItemCount(): Int = type.size

    override fun createFragment(position: Int): Fragment {
        return ProductComputerStoreFragment.newInstance(
            chosenAdapterSetFrom, type[position], objectComputerStore ?: ComputerStore()

        )
    }
}