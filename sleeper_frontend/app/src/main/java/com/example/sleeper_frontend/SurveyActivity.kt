package com.example.sleeper_frontend

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.sleeper_frontend.api.INetworkService
import com.example.sleeper_frontend.databinding.ActivitySurveyBinding
import com.example.sleeper_frontend.dto.register.RegisterRequest
import com.example.sleeper_frontend.dto.register.RegisterResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SurveyActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySurveyBinding
    private lateinit var goalSleepTime : String
    private lateinit var goalWakeTime : String

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

            tryNetwork()
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

        if (btn == binding.btnSurveyChooseStartTime) {
            goalSleepTime = getString(R.string.survey_scr_textview, hourOfDay, min)

            Log.d("hyeon", "goalSleepTime 초기화 완료")
        } else if (btn == binding.btnSurveyChooseEndTime) {
            goalWakeTime = getString(R.string.survey_scr_textview, hourOfDay, min)

            Log.d("hyeon", "goalWakeTime 초기화 완료")
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

    private fun getNetworkService(): INetworkService {
        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        val gson : Gson = GsonBuilder()
            .setLenient()
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.21.2:8082/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()

        return retrofit.create(INetworkService::class.java)
    }

    private fun tryNetwork() {
        Log.d("hyeon", "tryNetwork작동")
        val userId = intent.getStringExtra("userId").toString()

        val userPassword = intent.getStringExtra("userPassword").toString()
        val userNickName = intent.getStringExtra("userNickName").toString()
        val userAge = intent.getLongExtra("userAge", 1)
        val goalSleepTime = intent.getStringExtra("goalSleepTime").toString()
        val goalWakeTime = intent.getStringExtra("goalWakeTime").toString()

        Log.d("hyeon","변수 초기화")

        val initRequest = RegisterRequest(
            userId = userId + "",
            userPassword = userPassword + "",
            userNickName = userNickName + "",
            userAge = userAge,
            goalSleepTime = goalSleepTime + "",
            goalWakeTime = goalWakeTime + "" )

        val registerResponseCall : Call<RegisterResponse> = getNetworkService().getRegisterResponse(
            initRequest
        )

        Log.d("hyeon","call객체 초기화")

        registerResponseCall.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call : Call<RegisterResponse>, response: Response<RegisterResponse>) {
                Log.d("hyeon", "통신 성공")
                if (response.isSuccessful && response.body() != null) {

                    val result: RegisterResponse? = response.body()
                    val resultCode: String = response.code().toString()

                    Log.d("hyeon", resultCode)
                    val success: String = "200";
                    val badRequest: String = "300"
                    val internalServerError: String = "500"


                    if (resultCode == success) {
                        val intent = Intent(this@SurveyActivity, LoginActivity::class.java)
                        startActivity(intent)

                        finish()
                    }
                }
            }
            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Log.d("hyeon", "통신 실패")
                val string = t.message.toString()
                Log.d("hyeon", string)
                Log.d("hyeon", registerResponseCall.toString())
            }
        })
    }
}
