package com.hynekbraun.openweatherapi.weatherApi

import com.hynekbraun.openweatherapi.entities.current.CurrentWeatherEntity
import com.hynekbraun.openweatherapi.entities.forecast.ForecastWeatherEntity

interface WeatherApi {

    suspend fun getCurrentWeather(URL: String): CurrentWeatherEntity

    suspend fun getForecast(URL: String): ForecastWeatherEntity
}