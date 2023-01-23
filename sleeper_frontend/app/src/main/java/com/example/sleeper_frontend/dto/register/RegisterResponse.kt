package com.example.sleeper_frontend.dto.register

import androidx.annotation.Nullable
import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    @SerializedName("message")
    var message : String,
    @SerializedName("userPk")
    var userPk : Long?
)
