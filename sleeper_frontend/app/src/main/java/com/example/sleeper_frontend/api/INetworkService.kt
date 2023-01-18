package com.example.sleeper_frontend.api

import com.example.sleeper_frontend.dto.diary.SaveDiaryRequest
import com.example.sleeper_frontend.dto.diary.SaveDiaryResponse
import com.example.sleeper_frontend.dto.login.LoginRequest
import com.example.sleeper_frontend.dto.login.LoginResponse
import com.example.sleeper_frontend.dto.register.RegisterRequest
import com.example.sleeper_frontend.dto.register.RegisterResponse
import com.example.sleeper_frontend.dto.sleep.GetGoalTimeRequest
import com.example.sleeper_frontend.dto.sleep.GetGoalTimeResponse
import com.example.sleeper_frontend.dto.sleep.SetWakeTimeRequest
import com.example.sleeper_frontend.dto.sleep.SetWakeTimeResponse
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

    @PUT("sleeps/{userPk}/actualTime")
    fun putActualWakeTime(
        @Path("userPk") userPk : Long,
        @Body setWakeTimeRequset : SetWakeTimeRequest
    ): Call<SetWakeTimeResponse>

    @GET("users/time")
    fun getGoalTime(
        @Body getGoalTimeRequest: GetGoalTimeRequest
    ): Call<GetGoalTimeResponse>
}
