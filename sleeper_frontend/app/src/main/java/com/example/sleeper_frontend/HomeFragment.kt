package com.example.sleeper_frontend

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
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
            changeFragment()
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

    private fun changeFragment() {
        val diaryFragment = DiaryFragment()
        val transaction : FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()

        transaction?.replace(R.id.fl_container, diaryFragment)?.addToBackStack("HomeFragment")?.commit()
    }
}