package com.example.sleeper_frontend.api

import com.example.sleeper_frontend.dto.*
import retrofit2.Call
import retrofit2.http.*

interface INetworkService {

    @POST("localhost:8080/users")
    fun getRegisterResponse(
        @Body registerRequest : RegisterRequest
    ): Call<RegisterResponse>

    @POST("localhost:8080/login")
    fun getLoginResponse(
        @Body loginRequest : LoginRequest
    ): Call<LoginResponse>

}
