package com.hynekbraun.openweatherapi

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.hynekbraun.openweatherapi.databinding.ActivityMainBinding
import com.hynekbraun.openweatherapi.entities.CurrentWeatherEntity
import com.hynekbraun.openweatherapi.viewModel.MainViewModel
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)



        requestPermission()

//        viewModel.fetchWeather()
        viewModel.error.observe(this) {
            if (it.isNotEmpty()) {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.weatherLiveData.observe(this) {
            updateUI(it)
        }

        binding.mainBtnSubmit.setOnClickListener {

            //could put these two ifs together and make it one if????
            if (checkForNetwork()) {
                if (binding.mainEtCity.text.isEmpty()) {
                    Toast.makeText(this, "Please insert the name of the city", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    viewModel.fetchWeather(binding.mainEtCity.text.toString())
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
        binding.mainTvLastUpdate.text = getCurrentTime()
        binding.mainTvTemperature.text = roundTemp(currentWeather.main.temp) + "°C"
        binding.mainTvFeelsLike.text = roundTemp(currentWeather.main.feels_like) + "°C"
        binding.mainTvCondition.text = currentWeather.weather[0].description
        binding.mainTvHumidity.text = "Humidity :  ${currentWeather.main.humidity}"
        binding.mainTvWindSpeed.text = "Wind Speed: ${currentWeather.wind.speed}"
        binding.mainTvPressure.text = "Pressure: ${currentWeather.main.pressure}"
    }

    @SuppressLint("SimpleDateFormat")
    private fun getCurrentTime(): String {
        val date = Calendar.getInstance().time
        val formatter = SimpleDateFormat("EEE, hh:mm")
        return formatter.format(date)
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
        return isNetworkConnected()
    }

    private fun roundTemp(temp: Double): String {
        val df = DecimalFormat("#")
        df.roundingMode = RoundingMode.CEILING
        return df.format(temp)
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
        if (requestCode == 0 && grantResults.isNotEmpty()) {
            for (i in grantResults.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                }
            }

            getWeatherByLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getWeatherByLocation() {
        if (checkForNetwork() && checkForLocation())
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    // getting the last known or current location
                    binding.mainTvTemperature.text = location.latitude.toString()
                    viewModel.fetchWeatherByLocation(location.latitude, location.longitude)

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
}