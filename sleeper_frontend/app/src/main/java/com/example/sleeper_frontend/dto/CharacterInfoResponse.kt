package com.example.sleeper_frontend.dto

data class CharacterInfoResponse(
    var color : String,
    var status : String,
    var growth : GrowthModel,
    var speechBubble : String
)
