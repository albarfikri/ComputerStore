package com.albar.computerstore.ui.fragments.detail

import android.content.SharedPreferences
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.albar.computerstore.R
import com.albar.computerstore.data.remote.entity.ComputerStore
import com.albar.computerstore.databinding.FragmentComputerBinding
import com.albar.computerstore.others.Constants.PARCELABLE_KEY
import com.albar.computerstore.ui.adapter.ComputerStoreProductPagerAdapter
import com.albar.computerstore.ui.viewmodels.ComputerStoreProductViewModel
import com.albar.computerstore.ui.viewmodels.NetworkViewModel
import com.bumptech.glide.RequestManager
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class ComputerFragment : Fragment() {
    companion object{
        const val EXTRA_IS_ADMIN = "extra_is_admin"
    }

    private var _binding: FragmentComputerBinding? = null
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

    private val viewModel: ComputerStoreProductViewModel by viewModels()
    private val networkStatusViewModel: NetworkViewModel by viewModels()

    @Inject
    lateinit var glide: RequestManager

    @Inject
    lateinit var gson: Gson

    @Inject
    lateinit var sharedPref: SharedPreferences


    private var fabClicked = false


    private val tabTitles = mutableMapOf(
        "Computers" to R.drawable.ic_computer,
        "Accessories" to R.drawable.ic_accessories
    )

    private var id: String = ""

    private var objectComputerStoreComputerFragment: ComputerStore? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentComputerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTabAndViewPager()
        // fabButtons()
    }

    private fun setTabAndViewPager() {
        val isAdmin = arguments?.getBoolean(EXTRA_IS_ADMIN)
        objectComputerStoreComputerFragment = arguments?.getParcelable(PARCELABLE_KEY)

        val computerStoreProductPagerAdapter = ComputerStoreProductPagerAdapter(this)
        computerStoreProductPagerAdapter.apply {
            objectComputerStore = objectComputerStoreComputerFragment
            if (!isAdmin!!) {
                chosenAdapterSetFrom = adapterSetFrom[0]
            }else{
                chosenAdapterSetFrom = adapterSetFrom[2]
            }

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


//    private fun fabButtons() {
//        binding.apply {
//            btnGroup.setOnClickListener {
//                groupButtonClicked()
//            }
//
//            btnAdd.setOnClickListener {
//                val mBundle = Bundle()
//                mBundle.putString(AddOrUpdateFragment.EXTRA_ID_COMPUTER_STORE, id)
//                mBundle.putString(AddOrUpdateFragment.EXTRA_ACTION_TYPE, "add")
//                findNavController().navigate(
//                    R.id.action_memberFragment_to_addOrUpdateFragment,
//                    mBundle
//                )
//                fabClicked = false
//            }
//
//            btnSearch.setOnClickListener {
//                animationToSearchFragment()
//                fabClicked = false
//            }
//        }
//    }
//
//    private fun groupButtonClicked() {
//        setVisibility(fabClicked)
//        setAnimation(fabClicked)
//        fabClicked = !fabClicked
//    }
//
//    private fun setAnimation(clicked: Boolean) {
//        binding.apply {
//            if (!clicked) {
//                btnAdd.show()
//                btnSearch.show()
//            } else {
//                btnAdd.hide()
//                btnSearch.hide()
//            }
//        }
//    }
//
//    private fun setVisibility(clicked: Boolean) {
//        binding.apply {
//            if (!clicked) {
//                btnAdd.startAnimation(fromBottom)
//                btnSearch.startAnimation(fromBottom)
//                btnGroup.startAnimation(rotateOpen)
//            } else {
//                btnAdd.startAnimation(toBottom)
//                btnSearch.startAnimation(toBottom)
//                btnGroup.startAnimation(requireContext().rotateClose())
//            }
//        }
//    }
//
//    private fun animationToSearchFragment() {
//        val animation =
//            AnimationUtils.loadAnimation(requireContext(), R.anim.circle_explosion_anim).apply {
//                duration = 700
//                interpolator = AccelerateInterpolator()
//            }
//
//        with(binding) {
//            viewInterpolator.isInvisible = true
//            viewInterpolator.startAnimation(animation) {
//                findNavController().navigate(
//                    R.id.action_memberFragment_to_searchProductFragment
//                )
//                viewInterpolator.isInvisible = false
//            }
//        }
//    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}