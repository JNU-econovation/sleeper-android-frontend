package com.example.sleeper_frontend.dto.calendar

data class ShowDateResponse(
    var actualSleepTime : String,
    var actualWakeTime : String,
    var content : String,
    var diaryPk : Long,
    var link : InCalendarListModel,
    var setSleepTime : String,
    var setWakeTime : String
)
