package com.example.sleeper_frontend.screens.login

import android.text.Editable
import android.util.Log
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    //AutoLogin이 체크되어있었다 => userId, userPassword 가져오기 및 UI 보여주기
    //AutoLogin이 체크되어있지 않았다 => userIdPw는 null(hint가 표시되어야 함.)
    var isAutoLogin : Boolean = false
    var userId : Editable? = null
    var userPassword : Editable? = null

    init {
        getLoginInfo()
        Log.i("LoginViewModel", "LoginViewModel created!")
    }

    private fun getLoginInfo() {

    }


    fun saveLoginInfo() {

    }

    fun deleteLoginInfo() {

    }

    override fun onCleared() {
        super.onCleared()
        Log.i("LoginViewModel", "LoginViewModel destroyed!")
    }



}