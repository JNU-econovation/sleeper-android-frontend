package com.example.sleeper_frontend.dto.calendar

data class InCalendarListModel(
    var action : String,
    var href : String,
    var rel : String,
    var types : List<String>
)
