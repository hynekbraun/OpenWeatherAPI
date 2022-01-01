package com.hynekbraun.openweatherapi.weatherApi

import com.hynekbraun.openweatherapi.entities.CurrentWeatherEntity

interface WeatherApi {

    suspend fun getCurrentWeather(URL: String): CurrentWeatherEntity
}