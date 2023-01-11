package com.example.sleeper_frontend.dto

data class LoginResponse(
    var message : String,
    var accessToken : String,
    var refreshToken : String
)
