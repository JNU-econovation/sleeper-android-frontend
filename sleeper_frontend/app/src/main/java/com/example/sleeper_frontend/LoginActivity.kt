package com.example.sleeper_frontend

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.sleeper_frontend.api.INetworkService
import com.example.sleeper_frontend.databinding.ActivityLoginBinding
import com.example.sleeper_frontend.dto.login.LoginRequest
import com.example.sleeper_frontend.dto.login.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)

        val window = window
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.activityLoginLayout.setBackgroundResource(R.drawable.login_background)

        val sharedPref = getPreferences(Context.MODE_PRIVATE)

        if(sharedPref != null) {
            init(sharedPref)
        }

        binding.btnLogin.setOnClickListener {
            setLoginBtnListener()
        }

        binding.btnRegister.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun init(sp : SharedPreferences) {
        val userId = sp.getString("userId", "")
        val userPw = sp.getString("userPw", "")

        binding.editId.setText(userId)
        binding.editPw.setText(userPw)
    }

    private fun getNetworkService(): INetworkService {
        val retrofit = Retrofit.Builder()
            .baseUrl("localhost:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(INetworkService::class.java)
    }

    private fun setLoginBtnListener() {
        val userId : String = binding.editId.text.toString()
        val userPassword : String = binding.editPw.text.toString()

        val loginResponseCall = getNetworkService().getLoginResponse(LoginRequest(userId = userId, userPassword = userPassword))

        loginResponseCall.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call : Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val result : LoginResponse? = response.body()
                    val accessToken : String = result!!.accessToken
                    val refreshToken : String = result.refreshToken

                    val sharedPref = getPreferences(Context.MODE_PRIVATE)
                    sharedPref.edit().run {
                        putString("userId", userId)
                        putString("userPassword", userPassword)
                        commit()
                    }

                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)

                } else {


                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {}
        })
    }

}
