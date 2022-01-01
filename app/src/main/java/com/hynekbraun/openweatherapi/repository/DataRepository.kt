package com.hynekbraun.openweatherapi.repository

import com.hynekbraun.openweatherapi.entities.CurrentWeatherEntity
import com.hynekbraun.openweatherapi.weatherApi.WeatherApi

class DataRepository(private val weatherApi: WeatherApi) {

    suspend fun getWeather(Url: String): CurrentWeatherEntity {
        return weatherApi.getCurrentWeather(Url)
    }

}