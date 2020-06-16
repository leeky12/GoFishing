package com.ryalls.team.gofishing.ui.catch_entry

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.ryalls.team.gofishing.R
import com.ryalls.team.gofishing.data.WeatherData
import kotlinx.android.synthetic.main.catch_weather.*

/**
 * A placeholder fragment containing a simple view.
 */
class CatchWeather : Fragment() {

    private val viewModel: CatchDetailsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        retainInstance = true
        return inflater.inflate(R.layout.catch_weather, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.weatherPresent.observe(viewLifecycleOwner, Observer { weather ->
            if (viewModel.isNewRecord()) {
                rainField.setText(viewModel.todaysWeather.rain.toString())
                tempField.setText(viewModel.todaysWeather.temp.toString())
                humidityField.setText(viewModel.todaysWeather.humidity.toString())
                pressureField.setText(viewModel.todaysWeather.pressure.toString())
                cloudsField.setText(viewModel.todaysWeather.clouds.toString())
                descriptionField.setText(viewModel.todaysWeather.weatherDescription)
                windDirectionField.setText(viewModel.todaysWeather.windDirection.toString())
                windSpeedField.setText(viewModel.todaysWeather.windSpeed.toString())
                locationField.setText(viewModel.todaysLocation)
                Log.d("Volley", "Weather Present Observer called")
            }
        })
        if (!viewModel.isNewRecord()) {
            rainField.setText(viewModel.catchRecord.rain)
            tempField.setText(viewModel.catchRecord.temp)
            humidityField.setText(viewModel.catchRecord.humidity)
            pressureField.setText(viewModel.catchRecord.pressure)
            cloudsField.setText(viewModel.catchRecord.clouds)
            descriptionField.setText(viewModel.catchRecord.weatherDescription)
            windDirectionField.setText(viewModel.catchRecord.windDirection)
            windSpeedField.setText(viewModel.catchRecord.windSpeed)
            locationField.setText(viewModel.catchRecord.location)
        }

    }


    override fun onPause() {
        super.onPause()
        val weatherData = WeatherData
        weatherData.clouds = cloudsField.text.toString().toFloat()
        weatherData.humidity = humidityField.text.toString().toFloat()
//        viewModel.todaysLocation = locationField.text.toString()
        weatherData.pressure = pressureField.text.toString().toFloat()
        weatherData.rain = rainField.text.toString().toFloat()
        weatherData.weatherDescription = descriptionField.text.toString()
        weatherData.windDirection = windDirectionField.text.toString().toFloat()
        weatherData.windSpeed = windSpeedField.text.toString().toFloat()
        weatherData.temp = tempField.text.toString().toFloat()
        viewModel.updateWeather(weatherData)
        viewModel.updateLocation(locationField.text.toString())
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(sectionNumber: Int): CatchWeather {
            return CatchWeather().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}