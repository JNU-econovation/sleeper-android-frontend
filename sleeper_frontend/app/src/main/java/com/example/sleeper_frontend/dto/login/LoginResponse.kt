package com.example.sleeper_frontend.dto.login

data class LoginResponse(
    var message : String,
    var userPk : Long,
    var sleepPk : Long
)
