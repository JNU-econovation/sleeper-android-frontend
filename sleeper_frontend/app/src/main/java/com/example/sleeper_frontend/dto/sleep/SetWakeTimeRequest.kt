package com.example.sleeper_frontend.dto.sleep

data class SetWakeTimeRequest(
    var actualWakeTime : String,
    var userId : String
)
