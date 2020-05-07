package com.ryalls.team.gofishing.ui.catch_entry

import android.app.Application
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.ryalls.team.gofishing.data.WeatherData
import com.ryalls.team.gofishing.data.weather.GSONWeather
import com.ryalls.team.gofishing.persistance.CatchRecord
import com.ryalls.team.gofishing.persistance.CatchRepository
import com.ryalls.team.gofishing.persistance.CatchRoomDatabase
import com.ryalls.team.gofishing.utils.WeatherConvertor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*


class CatchDetailsViewModel(application: Application) : AndroidViewModel(application) {

    private var repository: CatchRepository

    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    var allWords: LiveData<List<CatchRecord>>
    lateinit var todaysWeather: WeatherData

    val weatherPresent: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    var catchRecord: CatchRecord = CatchRecord("")

    // Mutable String used to indicate the list has been completely filled  with the data
    val recordReady: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    init {
        val catchDao =
            CatchRoomDatabase.getDatabase(context = application, scope = viewModelScope).catchDao()
        repository = CatchRepository(catchDao)
        allWords = repository.allWords
    }

    fun getRecord(recordID: Int) {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                Log.d("TestCoroutine", "Started " + recordID)
                catchRecord = repository.getRecord(recordID)
                Log.d("TestCoroutine", "Finished")
            }
            withContext(Dispatchers.Main) {
                Log.d("TestCoroutine", "" + catchRecord.weight)
                recordReady.value = "True"
            }
        }
    }

    fun resetCatchDetails() {
        catchRecord.lure = ""
        catchRecord.structure = ""
        catchRecord.conditions = ""
        catchRecord.depth = ""
        catchRecord.hook = ""
        catchRecord.groundBait = ""
        catchRecord.boatspeed = ""
        catchRecord.tides = ""
        catchRecord.species = ""
        catchRecord.comments = ""
        catchRecord.weight = ""
        catchRecord.length = ""
        catchRecord.line = ""
        catchRecord.rod = ""
        catchRecord.reel = ""
        catchRecord.rain = ""
        catchRecord.clouds = ""
        catchRecord.humidity = ""
        catchRecord.pressure = ""
        catchRecord.temp = ""
        catchRecord.weatherDescription = ""
        catchRecord.windDirection = ""
        catchRecord.windSpeed = ""
        catchRecord.town = ""
    }

    fun updatesTackle(rod: String, reel: String, line: String) {
        catchRecord.line = line
        catchRecord.reel = reel
        catchRecord.rod = rod
    }

    fun updatesDetailsCatch(
        lure: String, structure: String, conditions: String, depth: String,
        hook: String, groundbait: String, boatspeed: String, tides: String
    ) {
        catchRecord.lure = lure
        catchRecord.structure = structure
        catchRecord.conditions = conditions
        catchRecord.depth = depth
        catchRecord.hook = hook
        catchRecord.groundBait = groundbait
        catchRecord.boatspeed = boatspeed
        catchRecord.tides = tides
    }

    fun updatesBasicCatch(species: String, comment: String, weight: String, length: String) {
        catchRecord.species = species
        catchRecord.comments = comment
        catchRecord.weight = weight
        catchRecord.length = length
    }

    fun updateWeather(
        rain: String, clouds: String, humidity: String, pressure: String, temp: String,
        weatherDescription: String, windDirection: String, windSpeed: String, town: String
    ) {
        catchRecord.rain = rain
        catchRecord.clouds = clouds
        catchRecord.humidity = humidity
        catchRecord.pressure = pressure
        catchRecord.temp = temp
        catchRecord.weatherDescription = weatherDescription
        catchRecord.windDirection = windDirection
        catchRecord.windSpeed = windSpeed
        catchRecord.town = town
    }

    fun updateLocation(town: String, latitude: String, longitude: String) {
//        catchRecord.town = town
        catchRecord.latitude = latitude
        catchRecord.longitude = longitude
    }

    fun update(updateRecord: CatchRecord) = viewModelScope.launch(Dispatchers.IO) {
        updateRecord.catchID = catchRecord.catchID
        Log.d("CatchRecord", "" + updateRecord.catchID)
        repository.update(updateRecord)
    }

    fun deleteRecord(catchID: Int) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteRecord(catchID)
    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(catchRecord: CatchRecord) = viewModelScope.launch(Dispatchers.IO) {
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        val date = Date()
        catchRecord.date = formatter.format(date)

        Log.d(
            "CatchRecord",
            catchRecord.species + " " + catchRecord.date + " " + catchRecord.location
        )
        repository.insert(catchRecord)
    }

    fun getWeather(context: Context, location: Location) {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                // Instantiate the RequestQueue.
                val queue = Volley.newRequestQueue(context)
                val url: String =
                    "https://api.openweathermap.org/data/2.5/weather?lat=" + location.latitude + "&lon=" + location.longitude + "&APPID=b4a56ac53a68780edf02ac7deb48b25e"
                // 51.2560767,-1.142331
                // Request a string response from the provided URL.
                val stringReq = StringRequest(
                    Request.Method.GET, url,
                    Response.Listener<String> { response ->
                        val gson = Gson()
                        try {
                            val wd = gson.fromJson(response, GSONWeather::class.java)
                            val weatherConv = WeatherConvertor()
                            todaysWeather = weatherConv.createWeatherData(wd)
                            weatherPresent.value = "True"
                            Log.d("Volley", "Got Data " + todaysWeather.weatherDescription)
                        } catch (jse: JsonSyntaxException) {
                            Log.d(
                                "Volley",
                                "com.ryalls.team.gofishing.data.weather.Weather not found " + jse.toString()
                            )
                        }
                    },
                    Response.ErrorListener { response ->
                        Log.d("Volley", "Didn't work " + response.toString())
                    })
                queue.add(stringReq)
            }
        }
    }
}


