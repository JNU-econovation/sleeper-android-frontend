package com.example.sleeper_frontend

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.sleeper_frontend.databinding.FragmentCalendarBinding
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener

class CalendarFragment : Fragment(R.layout.fragment_calendar) {


    private lateinit var binding: FragmentCalendarBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =  FragmentCalendarBinding.inflate(inflater, container, false)

        val todayDecorator = context?.let { TodayDecorator(it) }
        binding.calendar.addDecorators(todayDecorator)

        binding.calendar.setOnDateChangedListener { widget, date, selected ->

            binding.calendar.removeDecorator(todayDecorator)

            val selectedDate: CalendarDay = binding.calendar.selectedDate
            // eventDecorator는 선택된 날짜를 커스텀해주는 decorator

            val eventDecorator = context?.let { EventDecorator(it, selectedDate) }
            binding.calendar.addDecorator(eventDecorator)

            val calendarInsideFragment = CalendarInsideFragment()
            val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
            transaction.replace(R.id.fl_container, calendarInsideFragment).addToBackStack("CalendarFragment").commit()
        }

        return binding.root
    }

}