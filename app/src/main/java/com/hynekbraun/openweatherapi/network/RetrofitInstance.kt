package com.hynekbraun.openweatherapi.network

import com.hynekbraun.openweatherapi.util.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val builder = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val service = builder.create(RetrofitApi::class.java)
}