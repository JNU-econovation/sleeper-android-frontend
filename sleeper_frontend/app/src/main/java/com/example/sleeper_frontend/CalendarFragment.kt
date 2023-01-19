package com.example.sleeper_frontend

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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

        //다시 돌아왔을 때, selectedday에만 표시/


//        val todayDecorator = context?.let { TodayDecorator(it) }
//        binding.calendar.addDecorators(todayDecorator)

        lateinit var selectedDate : CalendarDay

        if(binding.calendar.selectedDate != null) {
            selectedDate = binding.calendar.selectedDate

            val eventDecorator = context?.let { EventDecorator(it, selectedDate) }
            binding.calendar.addDecorator(eventDecorator)
        }

        binding.calendar.setOnDateChangedListener { widget, date, selected ->

//            binding.calendar.removeDecorator(todayDecorator)

            selectedDate = binding.calendar.selectedDate

            val eventDecorator = context?.let { EventDecorator(it, selectedDate) }
            binding.calendar.addDecorator(eventDecorator)

            val bundle : Bundle = Bundle()
            val data = binding.calendar.selectedDate
            val year = data.year.toString()
            val temp = data.month + 1
            var month = temp.toString()

            if(month.length < 2) {
                month = "0${month}"
            }

            val day = data.day.toString()


            bundle.putString("year", year)
            bundle.putString("month", month)
            bundle.putString("day",day)

            val calendarInsideFragment = CalendarInsideFragment()
            calendarInsideFragment.arguments = bundle
            val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
            transaction.replace(R.id.fl_container, calendarInsideFragment).addToBackStack("CalendarFragment").commit()
        }

        return binding.root
    }

}