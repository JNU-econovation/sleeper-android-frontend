package com.example.sleeper_frontend.screens.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.sleeper_frontend.MainActivity
import com.example.sleeper_frontend.R
import com.example.sleeper_frontend.RegisterActivity
import com.example.sleeper_frontend.api.INetworkService
import com.example.sleeper_frontend.databinding.ActivityLoginBinding
import com.example.sleeper_frontend.dto.login.LoginRequest
import com.example.sleeper_frontend.dto.login.LoginResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)

        val window = window
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.i("GameFragment", "Called ViewModelProvider.get")
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        binding.activityLoginLayout.setBackgroundResource(R.drawable.login_background)

        binding.btnLoginAuto.isChecked = viewModel.isAutoLogin
        binding.editLoginUserId.text = viewModel.userId
        binding.editLoginUserId.text = viewModel.userPassword

        binding.btnLogin.setOnClickListener {

            if(binding.btnLoginAuto.isChecked) {
                viewModel.saveLoginInfo()
            } else {
                viewModel.deleteLoginInfo()
            }
            doLogin()

        }

        binding.btnRegister.setOnClickListener {

            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)

        }
    }

    private fun getNetworkService(): INetworkService {
        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .cookieJar(JavaNetCookieJar(CookieManager()))
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

    private fun doLogin() {
        val userId : String = binding.editLoginUserId.text.toString()
        val userPassword : String = binding.editLoginUserPw.text.toString()

        val loginResponseCall = getNetworkService().getLoginResponse(LoginRequest(userId = userId, userPassword = userPassword))

        loginResponseCall.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call : Call<LoginResponse>, response: Response<LoginResponse>) {
                Log.d("로그인 통신", "통신 상태 : 성공")
                if (response.isSuccessful && response.body() != null) {
                    val result : LoginResponse? = response.body()

                    val accessToken = response.headers()["authorization"]
                    val refreshToken = response.headers()["set-cookie"]

                    val userPk : Long = result!!.userPk
                    val sleepPk : Long = result.sleepPk

                    if (response.code() == 200) {

                        //자동 로그인
                        val loginPref = getPreferences(Context.MODE_PRIVATE)
                        loginPref.edit().run {
                            putString("userId", userId)
                            putString("userPw", userPassword)
                            commit()
                        }

                        //필요 정보 저장
                        val sharedPref = getSharedPreferences("user_info", Context.MODE_PRIVATE)
                        sharedPref.edit().run{
                            putLong("userPk", userPk)
                            putString("accessToken", accessToken)
                            putString("refreshToken", refreshToken)//삭제 예정
                            putLong("sleepPk", sleepPk)
                            commit()
                        }

                        //intent 전환
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)

                        finish()
                    }
                } else {
                    Log.d("로그인 통신", "통신 상태 : 비정상 통신")
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.d("로그인 통신", "통신 상태 : 실패")
                val string = t.message.toString()
                Log.d("예외 메세지", string)
                Log.d("요청 내용", loginResponseCall.toString())
            }
        })
    }

}
