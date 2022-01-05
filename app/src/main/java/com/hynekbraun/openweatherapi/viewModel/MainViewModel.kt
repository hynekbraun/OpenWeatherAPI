package com.hynekbraun.openweatherapi.viewModel

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hynekbraun.openweatherapi.entities.current.CurrentWeatherEntity
import com.hynekbraun.openweatherapi.entities.forecast.ForecastWeatherEntity
import com.hynekbraun.openweatherapi.network.RetrofitInstance
import com.hynekbraun.openweatherapi.repository.DataRepository
import com.hynekbraun.openweatherapi.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.lang.Exception
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel : ViewModel() {
    private val dataRepository = DataRepository(RetrofitInstance.service)

    val weatherLiveData = MutableLiveData<CurrentWeatherEntity>()
    val forecastLiveData = MutableLiveData<ForecastWeatherEntity>()
    val error = MutableLiveData<String>()

    fun fetchWeather(city: String) {
        viewModelScope.launch {
            try {
                val weather = withContext(Dispatchers.IO) {
                    dataRepository.getWeather("weather?q=$city&units=${Constants.UNIT_METRIC}&appid=${Constants.KEY}")
                }
                weatherLiveData.value = weather
            } catch (e: HttpException) {
                error.value = "$e"

            } catch (e: Exception) {
                error.value = "$e"
            }
        }
    }

    fun fetchWeatherByLocation(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val weather = withContext(Dispatchers.IO) {
                    dataRepository.getWeather("weather?lat=$lat&lon=$lon&units=${Constants.UNIT_METRIC}&appid=${Constants.KEY}")
                }
                weatherLiveData.value = weather
            } catch (e: HttpException) {
                error.value = "$e"

            } catch (e: Exception) {
                error.value = "$e"
            }
        }
    }

    fun fetchForecastByLocation(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val weather = withContext(Dispatchers.IO) {
                    dataRepository.getForecast("forecast?lat=$lat&lon=$lon&units=${Constants.UNIT_METRIC}&appid=${Constants.KEY}")
                }
                forecastLiveData.value = weather
            } catch (e: HttpException) {
                error.value = "$e"

            } catch (e: Exception) {
                error.value = "$e"
            }
        }
    }

    fun getForecast(city: String) {
        viewModelScope.launch {
            try {
                val weather = withContext(Dispatchers.IO) {
                    dataRepository.getForecast("forecast?q=$city&units=${Constants.UNIT_METRIC}&appid=${Constants.KEY}")
                }
                forecastLiveData.value = weather
            } catch (e: HttpException) {
                error.value = "$e"

            } catch (e: Exception) {
                error.value = "$e"
            }
        }
    }
    @SuppressLint("SimpleDateFormat")
     fun getCurrentTime(): String {
        val date = Calendar.getInstance().time
        val formatter = SimpleDateFormat("EEE, hh:mm")
        return formatter.format(date)
    }
     fun roundTemp(temp: Double): String {
        val df = DecimalFormat("#")
        df.roundingMode = RoundingMode.CEILING
        return df.format(temp)
    }
}
