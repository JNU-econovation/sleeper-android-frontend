package com.example.sleeper_frontend.dto.sleep

data class getGoalTimeResponse(
    var goalSleepTime : String,
    var goalWakeTime : String,
    var suggestedWakeTimes : List<String>
)
