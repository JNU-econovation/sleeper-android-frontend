package com.example.sleeper_frontend.dto.sleep

import java.time.LocalTime

data class SetGoalTimeRequest(
    var goalSleepTime : String,
    var goalWakeTime : String,
    var userId : String
)
