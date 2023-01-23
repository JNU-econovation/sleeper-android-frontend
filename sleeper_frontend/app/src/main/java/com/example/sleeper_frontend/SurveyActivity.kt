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
            doRegister()
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
            goalSleepTime = if(hourOfDay.toString().length < 2) {
                "0$hourOfDay:$min"
            } else {
                "$hourOfDay:$min"
            }
            Log.d("goalSleepTime 초기화 값", goalSleepTime)

        } else if (btn == binding.btnSurveyChooseEndTime) {
            goalWakeTime = if(hourOfDay.toString().length < 2) {
                "0$hourOfDay:$min"
            } else {
                "$hourOfDay:$min"
            }
            Log.d("goalWakeTime 초기화 값", goalWakeTime)
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
            .baseUrl("http://192.168.0.110:8080/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()

        return retrofit.create(INetworkService::class.java)
    }

    private fun doRegister() {
        Log.d("회원 가입 통신", "통신 상태 : 시작")
        val userId = intent.getStringExtra("userId").toString()

        val userPassword = intent.getStringExtra("userPassword").toString()
        val userNickName = intent.getStringExtra("userNickName").toString()
        val userAge = intent.getLongExtra("userAge", 1)
        val goalSleepTime = goalSleepTime
        val goalWakeTime = goalWakeTime

        Log.d("회원 가입 통신","통신 상태 : 변수 초기화 완료")

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

        Log.d("회원 가입 통신","통신 상태 : call 객체 초기화")

        registerResponseCall.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call : Call<RegisterResponse>, response: Response<RegisterResponse>) {
                Log.d("회원 가입 통신", "통신 상태 : 성공")
                if (response.isSuccessful && response.body() != null) {
                    Log.d("data",response.toString())
                    val result: RegisterResponse? = response.body()
                    val resultCode: String = response.code().toString()

                    Log.d("회원 가입 통신", "결과 코드 - $resultCode")
                    val success: String = "201";
                    val badRequest: String = "300"
                    val internalServerError: String = "500"


                    if (resultCode == success) {
                        Log.d("회원 가입 통신", "통신 상태 : 정상 통신")

                        val intent = Intent(this@SurveyActivity, LoginActivity::class.java)
                        startActivity(intent)

                        finish()
                    }
                }
            }
            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Log.d("회원 가입 통신", "통신 상태 : 실패")
                val string = t.message.toString()
                Log.d("회원 가입 통신", "예외 메세지 - $string")
                Log.d("회원 가입 통신", "요청 내용 - $registerResponseCall")
            }
        })
    }
}
