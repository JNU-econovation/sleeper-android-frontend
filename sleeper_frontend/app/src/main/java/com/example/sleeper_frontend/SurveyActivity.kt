package com.example.sleeper_frontend

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.sleeper_frontend.databinding.ActivitySurveyBinding

class SurveyActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySurveyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)

        val window = window
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        binding = ActivitySurveyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.activitySurveyLayout.setBackgroundResource(R.drawable.login_background)

        binding.btnSurveyFinished.isEnabled = false

        binding.btnSurveyChooseStartTime.setOnClickListener {
            showTimePicker(it)
        }

        binding.btnSurveyChooseEndTime.setOnClickListener {
            showTimePicker(it)
        }

        binding.btnSurveyFinished.setOnClickListener {
            val intent = Intent(this@SurveyActivity, LoginActivity::class.java)
            startActivity(intent)

            finish()
        }

    }

    private fun showTimePicker(btn : View) {
        val timeSetListener =
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                setTimeText(btn, hourOfDay, minute)}

        val timepicker = TimePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar,timeSetListener, 8, 0, false)
        timepicker.window?.setBackgroundDrawableResource(android.R.color.transparent);

        timepicker.setTitle("목표 수면 시작 시간")
        timepicker.show()
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

        if (btn == binding.btnSurveyChooseStartTime) {
            binding.textviewSurveyStartTimeMeridiem.text =meridiem
            binding.textviewSurveyStarTime.text = getString(R.string.survey_scr_textview, hour, min)
        } else if (btn == binding.btnSurveyChooseEndTime) {
            binding.textviewSurveyEndTimeMeridiem.text = meridiem
            binding.textviewSurveyEndTime.text = getString(R.string.survey_scr_textview, hour, min)
        }

        if (binding.textviewSurveyStarTime.text != "" && binding.textviewSurveyEndTime.text != "") enableBtn()
    }

    private fun enableBtn() {
        binding.btnSurveyFinished.isEnabled = true
    }
}
