package com.hynekbraun.openweatherapi.entities.forecast

data class ForecastWeatherEntity(
    val city: City,
    val cnt: Int,
    val cod: String,
    val list: List<ForecastEntity>,
    val message: Int
)