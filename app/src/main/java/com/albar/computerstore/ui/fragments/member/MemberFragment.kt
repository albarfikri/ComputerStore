package com.albar.computerstore.ui.fragments.member

import android.app.AlertDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.albar.computerstore.R
import com.albar.computerstore.data.remote.entity.ComputerStore
import com.albar.computerstore.databinding.FragmentMemberBinding
import com.albar.computerstore.databinding.ViewConfirmationDialogBinding
import com.albar.computerstore.others.Constants
import com.albar.computerstore.others.show
import com.albar.computerstore.others.startAnimation
import com.albar.computerstore.others.toastShort
import com.albar.computerstore.ui.adapter.ComputerStoreProductPagerAdapter
import com.albar.computerstore.ui.fragments.member.AddOrUpdateFragment.Companion.EXTRA_ACTION_TYPE
import com.albar.computerstore.ui.fragments.member.AddOrUpdateFragment.Companion.EXTRA_ID_COMPUTER_STORE
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
class MemberFragment : Fragment() {
    private var _binding: FragmentMemberBinding? = null
    private val binding get() = _binding!!

    // Anim
    private val rotateOpen: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.rotate_open_anim
        )
    }
    private val rotateClose: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.rotate_close_anim
        )
    }
    private val fromBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.from_bottom_anim
        )
    }
    private val toBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.to_bottom_anim
        )
    }

    private var fabClicked = false

    private val viewModel: ComputerStoreViewModel by viewModels()

    @Inject
    lateinit var gson: Gson

    @Inject
    lateinit var sharedPref: SharedPreferences

    @Inject
    lateinit var glide: RequestManager

    private val tabTitles = mutableMapOf(
        "Computers" to R.drawable.ic_computer,
        "Accessories" to R.drawable.ic_accessories
    )

    private var id: String = ""

    private var objectComputerStoreComputerFragment: ComputerStore? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMemberBinding.inflate(inflater, container, false)
        popupMenu()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTabAndViewPager()
        setMemberIdentityFromSharedPref()
        fabButtons()
    }

    private fun setTabAndViewPager() {
        val computerStoreProductPagerAdapter = ComputerStoreProductPagerAdapter(this)
        computerStoreProductPagerAdapter.apply {
            objectComputerStore = objectComputerStoreComputerFragment
            chosenAdapterSetFrom = adapterSetFrom[1]
        }

        val titles = ArrayList(tabTitles.keys)
        binding.viewPager2.adapter = computerStoreProductPagerAdapter
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
                    findNavController().navigate(R.id.action_memberFragment_to_editMemberFragment)
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
                    findNavController().navigate(R.id.action_memberFragment_to_signinFragment)
                    dialog.dismiss()
                    toastShort("Log out successfully.")
                }, Constants.DELAY_TO_MOVE_ANOTHER_FRAGMENT)
            }
        }
        viewDialog.imgCancelAction.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun setMemberIdentityFromSharedPref() {
        val json = sharedPref.getString(Constants.COMPUTER_STORE_SESSION, "")
        val obj = gson.fromJson(json, ComputerStore::class.java)

        // save data id
        id = obj.id

        binding.apply {
            etUsername.text = obj.name
            glide
                .load(obj.image)
                .placeholder(R.drawable.ic_broke_image)
                .transform(CenterCrop(), RoundedCorners(10))
                .into(image)
        }
    }

    private fun fabButtons() {
        val json = sharedPref.getString(Constants.COMPUTER_STORE_SESSION, "")
        val obj = gson.fromJson(json, ComputerStore::class.java)
        binding.apply {
            btnGroup.setOnClickListener {
                groupButtonClicked()
            }

            btnAdd.setOnClickListener {
                val mBundle = Bundle()
                mBundle.putString(EXTRA_ID_COMPUTER_STORE, obj.id)
                mBundle.putString(EXTRA_ACTION_TYPE, "add")
                findNavController().navigate(
                    R.id.action_memberFragment_to_addOrUpdateFragment,
                    mBundle
                )
                fabClicked = false
            }

            btnSearch.setOnClickListener {
                animationToSearchFragment()
                fabClicked = false
            }
        }
    }

    private fun groupButtonClicked() {
        setVisibility(fabClicked)
        setAnimation(fabClicked)
        fabClicked = !fabClicked
    }

    private fun setAnimation(clicked: Boolean) {
        binding.apply {
            if (!clicked) {
                btnAdd.show()
                btnSearch.show()
            } else {
                btnAdd.hide()
                btnSearch.hide()
            }
        }
    }

    private fun setVisibility(clicked: Boolean) {
        binding.apply {
            if (!clicked) {
                btnAdd.startAnimation(fromBottom)
                btnSearch.startAnimation(fromBottom)
                btnGroup.startAnimation(rotateOpen)
            } else {
                btnAdd.startAnimation(toBottom)
                btnSearch.startAnimation(toBottom)
                btnGroup.startAnimation(rotateClose)
            }
        }
    }

    private fun animationToSearchFragment() {
        val json = sharedPref.getString(Constants.COMPUTER_STORE_SESSION, "")
        val obj = gson.fromJson(json, ComputerStore::class.java)
        val animation =
            AnimationUtils.loadAnimation(requireContext(), R.anim.circle_explosion_anim).apply {
                duration = 700
                interpolator = AccelerateInterpolator()
            }

        with(binding) {
            viewInterpolator.isInvisible = true
            viewInterpolator.startAnimation(animation) {
                val mBundle = Bundle()
                mBundle.putString(EXTRA_ID_COMPUTER_STORE, id)
                findNavController().navigate(
                    R.id.action_memberFragment_to_searchProductFragment,
                    Bundle().apply {
                        putString(SearchProductFragment.EXTRA_ID_COMPUTER_STORE_FOR_SEARCHING, obj.id)
                    }
                )
                viewInterpolator.isInvisible = false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}