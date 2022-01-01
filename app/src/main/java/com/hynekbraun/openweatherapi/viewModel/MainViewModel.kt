package com.hynekbraun.openweatherapi.viewModel

import android.provider.SyncStateContract
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hynekbraun.openweatherapi.entities.CurrentWeatherEntity
import com.hynekbraun.openweatherapi.network.RetrofitInstance
import com.hynekbraun.openweatherapi.repository.DataRepository
import com.hynekbraun.openweatherapi.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.lang.Exception

class MainViewModel : ViewModel() {
    private val dataRepository = DataRepository(RetrofitInstance.service)

    val weatherLiveData = MutableLiveData<CurrentWeatherEntity>()
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
}