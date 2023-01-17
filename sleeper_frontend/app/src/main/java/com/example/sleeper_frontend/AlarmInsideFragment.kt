package com.example.sleeper_frontend


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.sleeper_frontend.databinding.FragmentAlarmInsideBinding

class AlarmInsideFragment : Fragment(R.layout.fragment_alarm_inside) {

    private lateinit var binding: FragmentAlarmInsideBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAlarmInsideBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment

        binding.btnSetSleepTime.setOnClickListener {
            showTimePicker(it)
        }

        binding.btnSetWakeTime.setOnClickListener {
            showTimePicker(it)
        }

        binding.btnFinish.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack("AlarmFragment",
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )

            val alarmFragment = AlarmFragment()
            val transaction : FragmentTransaction = requireFragmentManager().beginTransaction()
            transaction
                .replace(R.id.fl_container, alarmFragment)
                .commit()
        }

        return binding.root
    }

    private fun showTimePicker(btn : View) {
        val bundle = Bundle()
        bundle.putString("key", "sample")

        val timePickerDialog = TimePickerDialogFragment()

        timePickerDialog.setOnClickedListener(object : TimePickerDialogFragment.ButtonClickListener {
            override fun onClicked(hour : Int, minute : Int) {
                setTimeText(btn, hour, minute)
            }
        })

        timePickerDialog.arguments = bundle

        activity?.supportFragmentManager?.let { fragmentManager ->
            timePickerDialog.show(
                fragmentManager,
                "tag"
            )}
    }


    private fun setTimeText(btn : View, hourOfDay : Int, minute : Int) {
        var min : String = minute.toString()

        if ( min.length < 2 ) {
            min = "0${min}"
        }

        val meridiem : String = if (hourOfDay > 12) {
            "오후"
        } else {
            "오전"
        }

        val hour : Int  = if (hourOfDay > 13) {
            hourOfDay - 12
        } else {
            hourOfDay
        }

        if (btn == binding.btnSetSleepTime) {
            binding.textviewAlarmInsideSleepMeridiem.text =meridiem
            binding.textviewAlarmInsideSleepTime.text = getString(R.string.survey_scr_textview, hour, min)
        } else if (btn == binding.btnSetWakeTime) {
            binding.textviewAlarmInsideWakeMeridiem.text = meridiem
            binding.textviewAlarmInsideWakeTime.text = getString(R.string.survey_scr_textview, hour, min)
        }
    }
}
