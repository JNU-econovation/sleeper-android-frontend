package com.example.sleeper_frontend.dto.sleep

data class SetAlarmTimeRequest(
    var sleepTime : String,
    var wakeTime : String,
    var userId : String
)
