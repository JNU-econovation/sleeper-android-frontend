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
import com.example.sleeper_frontend.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.btnShowMore.setOnClickListener {
            clickBtnPopup()
        }

        binding.btnStartSleep.setOnClickListener {
            val diaryFragment = DiaryFragment()
            val transaction : FragmentTransaction = requireFragmentManager().beginTransaction()
            transaction.replace(R.id.fl_container, diaryFragment).commit()
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