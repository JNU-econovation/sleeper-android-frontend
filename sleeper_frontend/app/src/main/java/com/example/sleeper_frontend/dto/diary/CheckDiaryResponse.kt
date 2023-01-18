package com.example.sleeper_frontend.dto.diary

data class CheckDiaryResponse(
    var diaryPk : Long,
    var content : String,
    var existence : Boolean
)
