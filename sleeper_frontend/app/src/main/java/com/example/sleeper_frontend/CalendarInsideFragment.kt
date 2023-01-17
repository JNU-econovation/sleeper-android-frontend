package com.example.sleeper_frontend

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.example.sleeper_frontend.databinding.FragmentCalendarBinding
import com.example.sleeper_frontend.databinding.FragmentCalendarInsideBinding


class CalendarInsideFragment : Fragment(R.layout.fragment_calendar_inside) {

    private lateinit var binding: FragmentCalendarInsideBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding =  FragmentCalendarInsideBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onStop() {

        activity?.supportFragmentManager?.popBackStack("CalendarFragment",
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )

        super.onStop()
    }

}