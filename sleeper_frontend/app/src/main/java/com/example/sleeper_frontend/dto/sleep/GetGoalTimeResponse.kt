package com.example.sleeper_frontend.dto.sleep

data class GetGoalTimeResponse(
    var goalSleepTime : String,
    var goalWakeTime : String,
    var suggestedWakeTimes : List<String>
)
