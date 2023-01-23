package com.example.sleeper_frontend.dto.diary

data class ContinueDiaryRequest(
    var content : String,
    var userPk : Long
)
