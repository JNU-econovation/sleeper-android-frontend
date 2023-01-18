package com.example.sleeper_frontend.api

import com.example.sleeper_frontend.dto.diary.*
import com.example.sleeper_frontend.dto.login.LoginRequest
import com.example.sleeper_frontend.dto.login.LoginResponse
import com.example.sleeper_frontend.dto.register.RegisterRequest
import com.example.sleeper_frontend.dto.register.RegisterResponse
import com.example.sleeper_frontend.dto.sleep.*
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

    @GET("check")
    fun checkDiaryExistence(
        @Query("userPk") userPk : Long
    ): Call<CheckDiaryResponse>

    @POST("diaries")
    fun getDiaryPk(
        @Body saveDiaryRequest: SaveDiaryRequest
    ): Call<SaveDiaryResponse>

    @PUT("sleeps/{userPk}/actualTime")
    fun putActualWakeTime(
        @Path("userPk") userPk : Long,
        @Body setWakeTimeRequest : SetWakeTimeRequest
    ): Call<SetWakeTimeResponse>

    @GET("diaries/{diaryPk}")
    fun updateDiary(
        @Path("diaryPk") diaryPk : Long,
        @Body updateDiaryRequest : UpdateDiaryRequest
    ): Call<UpdateDiaryResponse>

    @GET("sleeps/{userPk}/setTime")
    fun getSettingTime(): Call<GetSettingTimeResponse>

    @GET("sleeps/recommend")
    fun getRecommendTime(
        @Query("setSleepTime") setSleepTime : String
    ): Call<GetRecommendationResponse>

    @PUT("sleep/{userPk}/setTime")
    fun setAlarmTime(
        @Path("userPk") userPk : Long,
        @Body setAlarmTimeRequest : SetAlarmTimeRequest
    ): Call<SetAlarmTimeResponse>



}
