package com.albar.computerstore.ui.fragments.admin

import android.app.AlertDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.albar.computerstore.R
import com.albar.computerstore.data.remote.entity.ComputerStore
import com.albar.computerstore.databinding.FragmentAdministratorBinding
import com.albar.computerstore.databinding.ViewConfirmationDialogBinding
import com.albar.computerstore.others.Constants
import com.albar.computerstore.others.Constants.COMPUTER_STORE_SESSION
import com.albar.computerstore.others.show
import com.albar.computerstore.others.toastShort
import com.albar.computerstore.ui.adapter.AdministratorPagerAdapter
import com.albar.computerstore.ui.viewmodels.ComputerStoreViewModel
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class AdministratorFragment : Fragment() {
    private var _binding: FragmentAdministratorBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ComputerStoreViewModel by viewModels()

    @Inject
    lateinit var gson: Gson

    @Inject
    lateinit var sharedPref: SharedPreferences

    @Inject
    lateinit var glide: RequestManager

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
        setAdminIdentityFromSharedPref()
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
                    dialogBuilder()
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
        }
    }

    private fun dialogBuilder() {
        val viewDialog = ViewConfirmationDialogBinding.inflate(layoutInflater, null, false)

        val builder = AlertDialog.Builder(requireContext())
            .setView(viewDialog.root)

        val dialog = builder.create()
        dialog.setCancelable(false)
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        viewDialog.btnYes.setOnClickListener {
            toastShort("Exit")
            viewModel.logout {
                viewDialog.btnYes.text = ""
                viewDialog.btnProgressSignUp.show()
                Handler(Looper.getMainLooper()).postDelayed({
                    findNavController().navigate(R.id.action_administratorFragment_to_signinFragment)
                    dialog.dismiss()
                    toastShort("Log out successfully.")
                }, Constants.DELAY_TO_MOVE_ANOTHER_FRAGMENT)
            }
        }
        viewDialog.imgCancelAction.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun setAdminIdentityFromSharedPref() {
        val json = sharedPref.getString(COMPUTER_STORE_SESSION, "")
        val obj = gson.fromJson(json, ComputerStore::class.java)

        binding.apply{
            etUsername.text = obj.username
            glide
                .load(obj.image)
                .placeholder(R.drawable.ic_broke_image)
                .transform(CenterCrop(), RoundedCorners(10))
                .into(image)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}