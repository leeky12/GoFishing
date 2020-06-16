package com.ryalls.team.gofishing.utils

import com.ryalls.team.gofishing.data.WeatherData
import com.ryalls.team.gofishing.data.weather.GSONWeather

class WeatherConvertor {
    fun createWeatherData(wd: GSONWeather) : WeatherData {
        val kelvinConvert = 273.15f
        val calcTemp = wd.main.temp.toFloat() - kelvinConvert
        val weatherData = WeatherData
        weatherData.clouds = wd.clouds.all.toFloat()
        weatherData.temp = calcTemp
        weatherData.humidity = wd.main.humidity.toFloat()
        weatherData.pressure = wd.main.pressure.toFloat()
        weatherData.windDirection = wd.wind.deg.toFloat()
        weatherData.windSpeed = wd.wind.speed.toFloat()
        weatherData.weatherDescription = wd.weather[0].description
//        weatherData.location = wd.name
        return weatherData
    }
}