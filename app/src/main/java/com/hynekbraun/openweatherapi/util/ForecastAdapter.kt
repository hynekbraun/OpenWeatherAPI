package com.hynekbraun.openweatherapi.util

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.hynekbraun.openweatherapi.R
import com.hynekbraun.openweatherapi.entities.forecast.ForecastEntity
import com.hynekbraun.openweatherapi.entities.forecast.ForecastWeatherEntity
import java.math.RoundingMode
import java.text.DecimalFormat

class ForecastAdapter : RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>() {
    var forecastList = arrayListOf<ForecastEntity>()

    class ForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var dateView: TextView? = null
        private var temperatureView: TextView? = null
        private var conditionView: TextView? = null
        private var iconView: ImageView? = null

        init {
            dateView = itemView.findViewById(R.id.fc_item_tv_date)
            temperatureView = itemView.findViewById(R.id.fc_item_tv_condition)
            conditionView = itemView.findViewById(R.id.fc_item_tv_temp)
            iconView = itemView.findViewById(R.id.fc_item_iv_icon)
        }

        @SuppressLint("SetTextI18n")
        fun bind(weather: ForecastEntity) {
            dateView?.text = weather.dt_txt
            temperatureView?.text = roundTemp(weather.main.temp) + "°C"
            conditionView?.text = weather.weather[0].main
            iconView?.load("http://openweathermap.org/img/wn/${weather.weather[0].icon}@2x.png")
        }

        private fun roundTemp(temp: Double): String {
            val df = DecimalFormat("##")
            df.roundingMode = RoundingMode.CEILING
            return df.format(temp)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val inflater =
            LayoutInflater.from(parent.context).inflate(R.layout.forecast_item, parent, false)
        return ForecastViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        holder.bind(forecastList[position])
    }

    override fun getItemCount() = forecastList.size

    fun getForecast(list: ForecastWeatherEntity) {
        forecastList = list.list as ArrayList<ForecastEntity>
        notifyDataSetChanged()
    }
}