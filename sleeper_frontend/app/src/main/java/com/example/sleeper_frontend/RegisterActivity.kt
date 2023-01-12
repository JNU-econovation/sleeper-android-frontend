package com.example.sleeper_frontend

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.NumberPicker
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.example.sleeper_frontend.databinding.ActivityRegisterBinding
import java.util.regex.Pattern
import kotlin.properties.Delegates

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding : ActivityRegisterBinding
    private lateinit var userId : String
    private lateinit var userPassword : String
    private lateinit var userNickName : String
    private var userAge by Delegates.notNull<Long>()

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

        binding.editId.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            val idPattern = "^[a-zA-Z]{8,20}$"
            val userId = binding.editId.text

            if(!Pattern.matches(idPattern, userId) && !hasFocus) {
                Toast.makeText(this@RegisterActivity, "아이디는 8~20자 영문 입력만 가능합니다.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.editPassword.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            val pwPattern = "^.*(?=^.{8,20}$)(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&;]).*$"
            val userPw = binding.editPassword.text

            if(!Pattern.matches(pwPattern, userPw) && !hasFocus) {
                Toast.makeText(this@RegisterActivity, "비밀번호 조건을 다시 확인해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.editPasswordIdentify.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            val userPw = binding.editPassword.text.toString()
            val userPwCheck = binding.editPasswordIdentify.text.toString()

            if(userPw != userPwCheck && !hasFocus) {
                Toast.makeText(this@RegisterActivity, "입력한 비밀번호와 맞지 않습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.editNickname.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            val nickNamePattern = "^[가-힣]{3,8}$"
            val userNickName = binding.editNickname.text

            if(!Pattern.matches(nickNamePattern, userNickName) && !hasFocus) {
                Toast.makeText(this@RegisterActivity, "닉네임은 3~8자리 한글만 가능합니다.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.editAge.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            val nickNamePattern = "^[0-9]{1,2}$"
            val userNickName = binding.editNickname.text


            if(!Pattern.matches(nickNamePattern, userNickName) && !hasFocus) {
                Toast.makeText(this@RegisterActivity, "나이를 바르게 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
