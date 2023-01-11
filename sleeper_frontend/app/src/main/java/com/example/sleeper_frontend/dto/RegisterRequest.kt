package com.example.sleeper_frontend.dto

data class RegisterRequest(
    var userId : String,
    var userPassword : String,
    var userNickName : String,
    var userAge : Long
)
