package com.example.sleeper_frontend.dto.sleep

data class GetRecommendationResponse(
    var timeStamp : String,
    var status : Long,
    var error : String,
    var message : String,
    var path : String
)
