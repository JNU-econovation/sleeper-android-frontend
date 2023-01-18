package com.example.sleeper_frontend.dto.login

data class LoginResponse(
    var message : String,
    var accessToken : String,
    var refreshToken : String,
    var userPk : Long
)
