package com.hynekbraun.openweatherapi.entities.forecast

data class Weather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)