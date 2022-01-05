package com.hynekbraun.openweatherapi

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.hynekbraun.openweatherapi.databinding.ActivityMainBinding
import com.hynekbraun.openweatherapi.entities.current.CurrentWeatherEntity
import com.hynekbraun.openweatherapi.util.ForecastAdapter
import com.hynekbraun.openweatherapi.viewModel.MainViewModel
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val adapter: ForecastAdapter by lazy { ForecastAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        binding.mainRv.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.mainRv.adapter = adapter

        requestPermission()
        viewModel.error.observe(this) {
            if (it.isNotEmpty()) {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.weatherLiveData.observe(this) {
            updateUI(it)
        }
        viewModel.forecastLiveData.observe(this) {
            adapter.getForecast(it)
        }

        binding.mainBtnSubmit.setOnClickListener {

            //could put these two ifs together and make it one if????
            if (checkForNetwork()) {
                if (binding.mainEtCity.text.isEmpty()) {
                    Toast.makeText(this, "Please insert the name of the city", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    viewModel.fetchWeather(binding.mainEtCity.text.toString())
                    viewModel.getForecast(binding.mainEtCity.text.toString())
                }
            } else {
                AlertDialog.Builder(this).setTitle("No Internet Connection")
                    .setMessage("Please check your internet connection and try again")
                    .setPositiveButton(android.R.string.ok) { _, _ -> }
                    .setIcon(android.R.drawable.ic_dialog_alert).show()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(currentWeather: CurrentWeatherEntity) {
        binding.mainTvLastUpdate.text = viewModel.getCurrentTime()
        binding.mainTvTemperature.text = viewModel.roundTemp(currentWeather.main.temp) + "°C"
        binding.mainTvFeelsLike.text = viewModel.roundTemp(currentWeather.main.feels_like) + "°C"
        binding.mainTvCondition.text = currentWeather.weather[0].description
        binding.mainTvHumidity.text = "Humidity :  ${currentWeather.main.humidity}"
        binding.mainTvWindSpeed.text = "Wind Speed: ${currentWeather.wind.speed}"
        binding.mainTvPressure.text = "Pressure: ${currentWeather.main.pressure}"
        binding.mainIvImage.load("http://openweathermap.org/img/wn/${currentWeather.weather[0].icon}@2x.png")
        binding.mainTvSlash.visibility = View.VISIBLE
    }

    private fun requestPermission() {
        //create val that will hold all the permissions that the user hasn agreed to yet
        val permissionsToRequest = mutableListOf<String>()
        //ask for each permision seperate and if they were not granted, add them to the list with requests that we need user to agree to
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        //check if we have anything to give to the request
        if (permissionsToRequest.isNotEmpty()) {
            //create request
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), 0)

        } else {
            getWeatherByLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        getWeatherByLocation()
    }

    @SuppressLint("MissingPermission")
    private fun getWeatherByLocation() {
        if (checkForNetwork() && checkForLocation())
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    // getting the last known or current location
                    viewModel.fetchWeatherByLocation(location.latitude, location.longitude)
                    viewModel.fetchForecastByLocation(location.latitude, location.longitude)
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this, "Failed on getting current location",
                        Toast.LENGTH_SHORT
                    ).show()
                }
    }

    private fun checkForLocation(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
            return false
        } else {
            return true
        }
    }

    private fun checkForNetwork(): Boolean {
        fun isNetworkConnected(): Boolean {
            //1
            val connectivityManager =
                getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            //2
            val activeNetwork = connectivityManager.activeNetwork
            //3
            val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
            //4
            return networkCapabilities != null &&
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }
        if (!isNetworkConnected()) {
            Toast.makeText(this, "Problem with internet", Toast.LENGTH_SHORT).show()
        }
        return isNetworkConnected()
    }
}