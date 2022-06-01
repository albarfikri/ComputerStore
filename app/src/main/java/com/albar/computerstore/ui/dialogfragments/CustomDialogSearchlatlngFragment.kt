package com.albar.computerstore.ui.dialogfragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.albar.computerstore.databinding.FragmentCustomDialogSearchlatlngBinding

class CustomDialogSearchlatlngFragment : DialogFragment() {
    private var _binding: FragmentCustomDialogSearchlatlngBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCustomDialogSearchlatlngBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}