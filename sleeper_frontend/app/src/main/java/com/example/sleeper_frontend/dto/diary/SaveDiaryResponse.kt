package com.example.sleeper_frontend.dto.diary

data class SaveDiaryResponse(
    var diaryPk : Long,
    var content : String,
    var savingDate : String,
    var writingTime : String
)
