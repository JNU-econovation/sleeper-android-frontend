package com.example.sleeper_frontend

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sleeper_frontend.databinding.FragmentCalendarBinding

class CalendarFragment : Fragment(R.layout.fragment_calendar) {


    private lateinit var binding: FragmentCalendarBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =  FragmentCalendarBinding.inflate(inflater, container, false)

        return binding.root
    }

}