package com.example.sleeper_frontend.api

import com.example.sleeper_frontend.dto.CharacterInfoResponse
import com.example.sleeper_frontend.dto.calendar.ShowDateResponse
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

    @POST("sleeps")
    fun resetSettingTime(
        @Query("userPk") userPk : Long
    ):Call<ResetSettingTimeResponse>

    @GET("check")
    fun checkDiaryExistence(
        @Query("userPk") userPk : Long
    ): Call<CheckDiaryResponse>

    @GET("character/{userpk}")
    fun getCharacterInfo(
        @Path("userpk") userpk : Long,
        @Query("userPk") userPk : Long
    ): Call<CharacterInfoResponse>

    @POST("diaries")
    fun getDiaryPk(
        @Body saveDiaryRequest: SaveDiaryRequest
    ): Call<SaveDiaryResponse>

    @PUT("sleeps/{sleepPk}/actualTime")
    fun putActualWakeTime(
        @Path("sleepPk") sleepPk : Long,
        @Body setWakeTimeRequest : SetWakeTimeRequest
    ): Call<SetWakeTimeResponse>

    @GET("diaries/{diaryPk}")
    fun updateDiary(
        @Path("diaryPk") diaryPk : Long,
        @Body updateDiaryRequest : UpdateDiaryRequest
    ): Call<UpdateDiaryResponse>

    @GET("sleeps/{userPk}/setTime")
    fun getSettingTime(
        @Path("userPk") userPk : Long
    ): Call<GetSettingTimeResponse>

    @GET("sleeps/recommend")
    fun getRecommendTime(
        @Query("setSleepTime") setSleepTime : String
    ): Call<GetRecommendationResponse>

    @PUT("sleeps/{userPk}/setTime")
    fun setAlarmTime(
        @Path("userPk") userPk : Long,
        @Body setAlarmTimeRequest : SetAlarmTimeRequest
    ): Call<SetAlarmTimeResponse>

    @GET("calendar/{date}")
    fun getCalendarInside(
        @Path("date") date : String,
        @Query("userPk") userPk : Long
    ): Call<ShowDateResponse>

    @DELETE("diaries/{diaryPk}")
    fun deleteDiary(
        @Path("diaryPk") diaryPk : Long,
        @Query("userPk") userPk : Long
    ): Call<DeleteDiaryResponse>

    @PUT("diaries/{diaryPk}/continue")
    fun continueDiary(
        @Path("diaryPk") diaryPk : Long,
    ): Call<ContinueDiaryResponse>

}
