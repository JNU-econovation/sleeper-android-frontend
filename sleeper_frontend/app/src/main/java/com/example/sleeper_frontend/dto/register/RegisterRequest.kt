package com.example.sleeper_frontend.dto.register

data class RegisterRequest(
    var userId: String,
    var userPassword: String,
    var userNickName: String,
    var userAge: Long/*,
    var goalSleepTime: String,
    var goalWakeTime: String*/
)
