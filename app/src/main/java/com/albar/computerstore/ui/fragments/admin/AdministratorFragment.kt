package com.albar.computerstore.ui.fragments.admin

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.albar.computerstore.R
import com.albar.computerstore.databinding.FragmentAdministratorBinding
import com.albar.computerstore.others.toastShort
import com.albar.computerstore.ui.adapter.AdministratorPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.math.roundToInt

class AdministratorFragment : Fragment() {
    private var _binding: FragmentAdministratorBinding? = null
    private val binding get() = _binding!!

    private val tabTitles = mutableMapOf(
        "Unverified" to R.drawable.ic_unverified,
        "Verified" to R.drawable.ic_verified
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdministratorBinding.inflate(inflater, container, false)
        popupMenu()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTabAndViewPager()
    }

    private fun setTabAndViewPager() {
        val titles = ArrayList(tabTitles.keys)
        binding.viewPager2.adapter = AdministratorPagerAdapter(this)
        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            tab.text = titles[position]
        }.attach()

        tabTitles.values.forEachIndexed { index, imageResId ->
            val textView =
                LayoutInflater.from(requireContext()).inflate(R.layout.tab_title, null) as TextView
            textView.setCompoundDrawablesWithIntrinsicBounds(imageResId, 0, 0, 0)
            textView.compoundDrawablePadding = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 4f, resources.displayMetrics
            ).roundToInt()
            binding.tabLayout.getTabAt(index)?.customView = textView
        }
    }

    private fun popupMenu() {
        val popupMenu = PopupMenu(requireContext(), binding.ivSettings)
        popupMenu.inflate(R.menu.popup_menu)
        popupMenu.setOnMenuItemClickListener { clicked ->
            when (clicked.itemId) {
                R.id.nav_edit -> {
                    toastShort("Head up to Edit")
                    true
                }
                R.id.nav_logout -> {
                    toastShort("Head up to Exit")
                    true
                }
                else -> true

            }
        }

        binding.ivSettings.setOnClickListener {
            try {
                val popup = PopupMenu::class.java.getDeclaredField("mPopup")
                popup.isAccessible = true
                val menu = popup.get(popupMenu)
                menu.javaClass
                    .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                    .invoke(menu, true)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                popupMenu.show()
            }

            true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}