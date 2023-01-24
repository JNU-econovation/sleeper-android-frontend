package com.example.sleeper_frontend.dto.sleep

import java.time.ZonedDateTime

data class SetAlarmTimeRequest(
    var setSleepTime : String,
    var setWakeTime : String,
    var userPk : Long
)
