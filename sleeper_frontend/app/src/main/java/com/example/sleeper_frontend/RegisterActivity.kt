package com.example.sleeper_frontend

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.example.sleeper_frontend.api.INetworkService
import com.example.sleeper_frontend.databinding.ActivityRegisterBinding
import com.example.sleeper_frontend.dto.login.LoginRequest
import com.example.sleeper_frontend.dto.login.LoginResponse
import com.example.sleeper_frontend.dto.register.RegisterRequest
import com.example.sleeper_frontend.dto.register.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Long.parseLong
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding : ActivityRegisterBinding
    private var checkArray : Array<Boolean> = Array(5) { false }

    override fun onCreate(savedInstanceState: Bundle?) {

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)

        val window = window
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.activityRegisterLayout.setBackgroundResource(R.drawable.login_background)

        binding.btnOkay.isEnabled = false

        addEditIdListener()

        addEditPwListener()

        addEditPwIdenListener()

        addEditNickNameListener()

        addEditAgeListener()

        addBtnOkayListener()

    }

    private fun addEditIdListener() {
        binding.editId.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            val idPattern = "^[a-zA-Z]{8,20}$"
            val userId = binding.editId.text

            if(!Pattern.matches(idPattern, userId) && !hasFocus) {

                Toast.makeText(this@RegisterActivity, "아이디는 8~20자 영문 입력만 가능합니다.", Toast.LENGTH_SHORT).show()

                checkArray[0] = false

            } else {

                checkArray[0] = true

            }
        }

        binding.editId.addTextChangedListener {
            enableBtnTrue()
            enableBtnFalse()
        }
    }

    private fun addEditPwListener() {
        binding.editPassword.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            val pwPattern = "^.*(?=^.{8,20}$)(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&;]).*$"
            val userPw = binding.editPassword.text

            if(!Pattern.matches(pwPattern, userPw) && !hasFocus) {
                Toast.makeText(this@RegisterActivity, "비밀번호 조건을 다시 확인해주세요.", Toast.LENGTH_SHORT).show()
                checkArray[1] = false

            } else {

                checkArray[1] = true

            }
        }

        binding.editPassword.addTextChangedListener {
            enableBtnTrue()
            enableBtnFalse()
        }
    }

    private fun addEditPwIdenListener() {
        binding.editPasswordIdentify.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            val userPw = binding.editPassword.text.toString()
            val userPwCheck = binding.editPasswordIdentify.text.toString()

            if(userPw != userPwCheck && !hasFocus) {
                Toast.makeText(this@RegisterActivity, "입력한 비밀번호와 맞지 않습니다.", Toast.LENGTH_SHORT).show()
                checkArray[2] = false

            } else {

                checkArray[2] = true

            }
        }

        binding.editPasswordIdentify.addTextChangedListener {
            enableBtnTrue()
            enableBtnFalse()
        }
    }

    private fun addEditNickNameListener() {

        binding.editNickname.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            val nickNamePattern = "^[가-힣]{3,8}$"
            val userNickName = binding.editNickname.text

            if(!Pattern.matches(nickNamePattern, userNickName) && !hasFocus) {
                Toast.makeText(this@RegisterActivity, "닉네임은 3~8자리 한글만 가능합니다.", Toast.LENGTH_SHORT).show()
                checkArray[3] = false

            } else {

                checkArray[3] = true

            }
        }

        binding.editNickname.addTextChangedListener {
            enableBtnTrue()
            enableBtnFalse()
        }
    }

    private fun addEditAgeListener() {
        binding.editAge.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            val agePattern = "^[0-9]{1,2}$"
            val userAge = binding.editAge.text

            if(!Pattern.matches(agePattern, userAge) && !hasFocus) {
                Toast.makeText(this@RegisterActivity, "나이를 바르게 입력해주세요.", Toast.LENGTH_SHORT).show()
                checkArray[4] = false

            } else {

                checkArray[4] = true

            }
        }

        binding.editAge.addTextChangedListener {
            enableBtnTrue()
            enableBtnFalse()
        }
    }

    private fun enableBtnTrue() {

        if(binding.editId.text.isEmpty()||
                binding.editPassword.text.isEmpty()||
                binding.editPasswordIdentify.text.isEmpty()||
                binding.editNickname.text.isEmpty()||
                binding.editAge.text.isEmpty()) {return}

        checkArray.forEach {
            if(!it) {
                return@enableBtnTrue
            }
        }

        binding.btnOkay.isEnabled = true
    }

    private fun enableBtnFalse() {
        checkArray.forEach {
            if(!it) {
                binding.btnOkay.isEnabled = false

                return@enableBtnFalse
            }
        }
    }

    private fun getNetworkService(): INetworkService {
        val retrofit = Retrofit.Builder()
            .baseUrl("localhost:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(INetworkService::class.java)
    }

    private fun addBtnOkayListener() {
        val userId : String = binding.editId.text.toString()
        val userPassword : String = binding.editPassword.text.toString()
        val userNickName : String = binding.editNickname.text.toString()
        val temp : String = binding.editAge.text.toString()
        val userAge : Long = parseLong(temp)

        val registerResponseCall = getNetworkService().getRegisterResponse(
            RegisterRequest(userId = userId, userPassword = userPassword,userNickName = userNickName, userAge = userAge)
        )

        registerResponseCall.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call : Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful) {

                    val result : RegisterResponse? = response.body()
                    val userId = result!!.userId

                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    startActivity(intent)

                } else {


                }
            }
            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {}
        })
    }

}
