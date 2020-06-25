package com.ryalls.team.gofishing.utils

import com.ryalls.team.gofishing.data.WeatherData
import com.ryalls.team.gofishing.data.weather.GSONWeather
import kotlin.math.pow
import kotlin.math.roundToInt

class WeatherConvertor {

    fun createWeatherData(wd: GSONWeather) : WeatherData {
        val kelvinConvert = 273.15f
        val temp = wd.main?.temp?.toFloat()
        val calcTemp = temp?.minus(kelvinConvert)?.roundTo(2)
        val weatherData = WeatherData
        weatherData.rain = (wd.rain?.get3H ?: 0.0).roundTo(2).toString()
        weatherData.clouds = (wd.clouds?.all ?: 0).toString()
        weatherData.temp = calcTemp?.roundTo(2).toString()
        weatherData.humidity = (wd.main?.humidity ?: 0).toString()
        weatherData.pressure = (wd.main?.pressure ?: 0).toString()
        weatherData.windDirection = (wd.wind?.deg ?: 0.0).roundTo(2).toString()
        weatherData.windSpeed = (wd.wind?.speed ?: 0.0).roundTo(2).toString()
        weatherData.weatherDescription = wd.weather?.get(0)?.description ?: ""
        return weatherData
    }

    private fun Double.roundTo(numFractionDigits: Int): Double {
        val factor = 10.0.pow(numFractionDigits.toDouble())
        return (this * factor).roundToInt() / factor
    }

    private fun Float.roundTo(numFractionDigits: Int): Double {
        val factor = 10.0.pow(numFractionDigits.toDouble())
        return (this * factor).roundToInt() / factor
    }

}