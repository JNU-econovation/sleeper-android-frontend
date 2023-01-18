package com.example.sleeper_frontend.api

import com.example.sleeper_frontend.dto.diary.SaveDiaryRequest
import com.example.sleeper_frontend.dto.diary.SaveDiaryResponse
import com.example.sleeper_frontend.dto.login.LoginRequest
import com.example.sleeper_frontend.dto.login.LoginResponse
import com.example.sleeper_frontend.dto.register.RegisterRequest
import com.example.sleeper_frontend.dto.register.RegisterResponse
import retrofit2.Call
import retrofit2.http.*

interface INetworkService {

    @POST("users")
    fun getRegisterResponse(
        @Body registerRequest : RegisterRequest
    ): Call<RegisterResponse>

    @POST("login")
    fun getLoginResponse(
        @Body loginRequest : LoginRequest
    ): Call<LoginResponse>

    @POST("diaries")
    fun getDiaryPk(
        @Body saveDiaryRequest: SaveDiaryRequest
    ): Call<SaveDiaryResponse>
}
