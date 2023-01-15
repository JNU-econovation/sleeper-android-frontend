package com.example.sleeper_frontend

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sleeper_frontend.databinding.FragmentAlarmInsideBinding

class AlarmInsideFragment : Fragment(R.layout.fragment_alarm_inside) {

    private lateinit var binding: FragmentAlarmInsideBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAlarmInsideBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

}