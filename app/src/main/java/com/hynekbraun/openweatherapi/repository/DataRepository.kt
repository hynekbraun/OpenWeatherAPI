package com.hynekbraun.openweatherapi.repository

import com.hynekbraun.openweatherapi.entities.current.CurrentWeatherEntity
import com.hynekbraun.openweatherapi.entities.forecast.ForecastWeatherEntity
import com.hynekbraun.openweatherapi.weatherApi.WeatherApi

class DataRepository(private val weatherApi: WeatherApi) {

    suspend fun getWeather(Url: String): CurrentWeatherEntity {
        return weatherApi.getCurrentWeather(Url)
    }
    suspend fun getForecast(Url: String): ForecastWeatherEntity{
        return weatherApi.getForecast(Url)
    }
}