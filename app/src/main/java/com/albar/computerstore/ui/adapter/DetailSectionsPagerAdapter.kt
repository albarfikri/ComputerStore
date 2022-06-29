package com.albar.computerstore.ui.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.albar.computerstore.data.remote.entity.ComputerStore
import com.albar.computerstore.others.Constants.PARCELABLE_KEY
import com.albar.computerstore.ui.fragments.detail.ComputerFragment
import com.albar.computerstore.ui.fragments.detail.OverviewFragment

class DetailSectionsPagerAdapter(fm: Fragment) : FragmentStateAdapter(fm) {
    var objectComputerStore: ComputerStore? = null

    var fragment: Fragment? = null

    private val totalFragment = arrayListOf(OverviewFragment(), ComputerFragment())

    override fun createFragment(position: Int): Fragment {

        when (position) {
            0 -> {
                fragment = totalFragment[position].apply {
                    this.arguments = Bundle().apply {
                        putParcelable(PARCELABLE_KEY, objectComputerStore)
                    }
                }
            }
            1 -> {
                fragment = totalFragment[position]
            }
        }
        return fragment as Fragment
    }

    override fun getItemCount(): Int = totalFragment.size

}

//override fun createFragment(position: Int): Fragment {
//
//    when (position) {
//        0 -> fragment = totalFragment[position].apply {
//            Bundle().apply {
//                putParcelable(PARCELABLE_KEY, objectComputerStore)
//            }
//        }
//        1 -> fragment = totalFragment[position]
//    }
//    return fragment as Fragment
//}