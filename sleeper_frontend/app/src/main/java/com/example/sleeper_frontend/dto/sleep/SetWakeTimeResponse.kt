package com.example.sleeper_frontend.dto.sleep

data class SetWakeTimeResponse(
    var message : String,
    var sleepPk : Long,
    var userPk : String
)
