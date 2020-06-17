package com.ryalls.team.gofishing.utils

import com.ryalls.team.gofishing.data.WeatherData
import com.ryalls.team.gofishing.data.weather.GSONWeather

class WeatherConvertor {
    fun createWeatherData(wd: GSONWeather) : WeatherData {
        val kelvinConvert = 273.15f
        val calcTemp = wd.main.temp.toFloat() - kelvinConvert
        val weatherData = WeatherData
        weatherData.clouds = wd.clouds.all.toString()
        weatherData.temp = calcTemp.toString()
        weatherData.humidity = wd.main.humidity.toString()
        weatherData.pressure = wd.main.pressure.toString()
        weatherData.windDirection = wd.wind.deg.toString()
        weatherData.windSpeed = wd.wind.speed.toString()
        weatherData.weatherDescription = wd.weather[0].description
        return weatherData
    }
}