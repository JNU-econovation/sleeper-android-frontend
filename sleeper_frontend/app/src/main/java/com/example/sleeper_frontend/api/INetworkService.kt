package com.example.sleeper_frontend.api

import com.example.sleeper_frontend.dto.character.CharacterInfoResponse
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

    @GET("diaries/check")
    fun checkDiaryExistence(
        @Header("authorization") accessToken: String,
        @Query("userPk") userPk : Long
    ): Call<CheckDiaryResponse>

    @GET("character/")
    fun getCharacterInfo(
        @Header("authorization") accessToken: String,
        @Query("userPk") userPk : Long
    ): Call<CharacterInfoResponse>

    @POST("diaries")
    fun getDiaryPk(
        @Header("authorization") accessToken: String,
        @Body saveDiaryRequest: SaveDiaryRequest
    ): Call<SaveDiaryResponse>


    @PUT("sleeps/{sleepPk}/actualTime")
    fun putActualWakeTime(
        @Header("authorization") accessToken: String,
        @Path("sleepPk") sleepPk : Long,
        @Body setWakeTimeRequest : SetWakeTimeRequest
    ): Call<SetWakeTimeResponse>


    @PUT("diaries/{diaryPk}/continue")
    fun continueDiary(
        @Header("authorization") accessToken: String,
        @Path("diaryPk") diaryPk : Long,
        @Body continueDiaryRequest : ContinueDiaryRequest
    ): Call<ContinueDiaryResponse>


    @PUT("diaries/{diaryPk}")
    fun updateDiary(
        @Header("authorization") accessToken: String,
        @Path("diaryPk") diaryPk : Long,
        @Body updateDiaryRequest: UpdateDiaryRequest
    ): Call<UpdateDiaryResponse>


    @GET("sleeps/{userPk}/setTime")
    fun getSettingTime(
        @Header("authorization") accessToken: String,
        @Path("userPk") userPk : Long
    ): Call<GetSettingTimeResponse>

    @GET("sleeps/recommend")
    fun getRecommendTime(
        @Header("authorization") accessToken: String,
        @Query("setSleepTime") setSleepTime : String
    ): Call<GetRecommendationResponse>

    @PUT("sleeps/{userPk}/setTime")
    fun setAlarmTime(
        @Header("authorization") accessToken: String,
        @Path("userPk") userPk : Long,
        @Body setAlarmTimeRequest : SetAlarmTimeRequest
    ): Call<SetAlarmTimeResponse>

    @GET("calendar/{date}")
    fun getCalendarInside(
        @Header("authorization") accessToken: String,
        @Path("date") date : String,
        @Query("userPk") userPk : Long
    ): Call<ShowDateResponse>

    @DELETE("diaries/{diaryPk}")
    fun deleteDiary(
        @Header("authorization") accessToken: String,
        @Path("diaryPk") diaryPk : Long,
        @Query("userPk") userPk : Long
    ): Call<DeleteDiaryResponse>


}
