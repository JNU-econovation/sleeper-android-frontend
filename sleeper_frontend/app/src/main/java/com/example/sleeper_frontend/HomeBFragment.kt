package com.example.sleeper_frontend

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import com.example.sleeper_frontend.databinding.FragmentHomeBBinding
import com.example.sleeper_frontend.databinding.FragmentHomeBinding

class HomeBFragment : Fragment(R.layout.fragment_home_b) {

    private lateinit var binding: FragmentHomeBBinding
    private val mainActivity = MainActivity()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeBBinding.inflate(inflater, container, false)

        binding.btnShowMore.setOnClickListener {
            clickBtnPopup()
        }
        binding.btnStopSleep.setOnClickListener {
            val homeFragment = HomeFragment()
            val transaction : FragmentTransaction = requireFragmentManager().beginTransaction()
            transaction.replace(R.id.fl_container, homeFragment).commit()
        }

        return binding.root
    }

    private fun clickBtnPopup() {
        val popup : PopupDialogFragment = PopupDialogFragment().getInstance()
        activity?.supportFragmentManager?.let { fragmentManager ->
            popup.show(
                fragmentManager,
                "tag"
            )
        }
    }
}