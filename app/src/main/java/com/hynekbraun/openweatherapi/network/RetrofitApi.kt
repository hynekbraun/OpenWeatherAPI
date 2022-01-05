package com.hynekbraun.openweatherapi.network

import com.hynekbraun.openweatherapi.entities.current.CurrentWeatherEntity
import com.hynekbraun.openweatherapi.entities.forecast.ForecastWeatherEntity
import com.hynekbraun.openweatherapi.weatherApi.WeatherApi
import retrofit2.http.GET
import retrofit2.http.Url

interface RetrofitApi: WeatherApi {

    @GET
    override suspend fun getCurrentWeather(@Url URL: String): CurrentWeatherEntity

    @GET
    override suspend fun getForecast(@Url URL: String): ForecastWeatherEntity
}