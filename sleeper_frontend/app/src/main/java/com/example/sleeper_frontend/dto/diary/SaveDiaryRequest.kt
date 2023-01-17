package com.example.sleeper_frontend.dto.diary

data class SaveDiaryRequest(
    var content : String,
    var userPk : Long
)
