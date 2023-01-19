package com.example.sleeper_frontend.dto.calendar

data class ShowDateResponse(
    var actualSleepTime : List<String>,
    var actualWakeTime : List<String>,
    var content : String,
    var diaryPk : Long,
    var setSleepTime : List<String>,
    var setWakeTime : List<String>
)
