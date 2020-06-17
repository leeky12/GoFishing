package com.ryalls.team.gofishing.ui.catch_entry

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.location.Geocoder
import android.location.Location
import android.util.Base64
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.OnSuccessListener
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.ryalls.team.gofishing.data.WeatherData
import com.ryalls.team.gofishing.data.weather.GSONWeather
import com.ryalls.team.gofishing.persistance.CatchRecord
import com.ryalls.team.gofishing.persistance.CatchRepository
import com.ryalls.team.gofishing.persistance.CatchRoomDatabase
import com.ryalls.team.gofishing.utils.Thumbnail
import com.ryalls.team.gofishing.utils.WeatherConvertor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class CatchDetailsViewModel(application: Application) : AndroidViewModel(application) {

    // 1hr in mill3seconds
    private val timeToWait: Long = 3600000

    private var repository: CatchRepository
    private var weatherCache: Long = 0L
    private var todaysLocation = ""

    private var lastLocation: Location? = null

    private var isNewRecord = true

    var allWords: LiveData<List<CatchRecord>>

    var todaysWeather: WeatherData = WeatherData

    val weatherPresent: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    // Mutable String used to indicate the catch locations list has been completely filled  with the data
    private val homeLocationReady: MutableLiveData<String> by lazy {
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

    fun setNewRecord(value: Boolean) {
        isNewRecord = value
    }

    fun isNewRecord(): Boolean {
        return isNewRecord
    }

    @SuppressLint("CheckResult")
    fun setThumbnail(currentPhotoPath: String) = runBlocking {
        // create a thumbnail in the background so it doesnt hold up anything
        val job = viewModelScope.launch {
            val bytearrayoutputstream = ByteArrayOutputStream()
            val thumbnail = Thumbnail().decodeSampledBitmap(currentPhotoPath, 100, 100)
            thumbnail?.compress(Bitmap.CompressFormat.JPEG, 70, bytearrayoutputstream)
            val bytes = bytearrayoutputstream.toByteArray()
            val base64 = Base64.encode(bytes, Base64.DEFAULT)
            catchRecord.thumbnail = String(base64)
            thumbnail?.recycle()
        }
        job.join()
    }

    fun getCatchRecord(recordID: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                Log.d("TestCoroutine", "Started $recordID")
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
        catchRecord.catchID = 0
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
        catchRecord.location = ""
        catchRecord.imageID = ""
        catchRecord.latitude = ""
        catchRecord.longitude = ""
        catchRecord.thumbnail = ""
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

    private fun updateLocation(latitude: String, longitude: String) {
        catchRecord.latitude = latitude
        catchRecord.longitude = longitude
    }

    fun updateRecord(updateRecord: CatchRecord) = viewModelScope.launch(Dispatchers.IO) {
        updateRecord.catchID = catchRecord.catchID
        Log.d("CatchRecord", "${updateRecord.catchID}")
        repository.update(updateRecord)
    }

    fun deleteRecord(catchID: Int) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteRecord(catchID)
    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insertRecord(catchRecord: CatchRecord) = viewModelScope.launch(Dispatchers.IO) {
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        val date = Date()
        catchRecord.date = formatter.format(date)

        Log.d(
            "CatchRecord",
            catchRecord.species + " " + catchRecord.date + " " + catchRecord.location
        )
        repository.insert(catchRecord)
    }

    private fun returnBlankIfZero(value: String): String {
        return if (value == "0.0") {
            ""
        } else {
            value
        }
    }

    fun updateWeather(todaysWeather: WeatherData) {
        catchRecord.rain = returnBlankIfZero(todaysWeather.rain)
        catchRecord.clouds = returnBlankIfZero(todaysWeather.clouds)
        catchRecord.humidity = returnBlankIfZero(todaysWeather.humidity)
        catchRecord.pressure = returnBlankIfZero(todaysWeather.pressure)
        catchRecord.temp = returnBlankIfZero(todaysWeather.temp)
        catchRecord.weatherDescription = todaysWeather.weatherDescription
        catchRecord.windDirection = returnBlankIfZero(todaysWeather.windDirection)
        catchRecord.windSpeed = returnBlankIfZero(todaysWeather.windSpeed)
    }

    fun getTodaysLocation(): String {
        return todaysLocation
    }

    fun updateLocation(location: String) {
        catchRecord.location = location
    }

    private fun getWeather(context: Context, location: Location) {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(context)

        val url =
            "https://api.openweathermap.org/data/2.5/weather?lat=${location.latitude}&lon=${location.longitude}&APPID=b4a56ac53a68780edf02ac7deb48b25e"

        // Request a string response from the provided URL.
        val stringReq = StringRequest(
            Request.Method.GET, url,
            Response.Listener { response ->
                val gson = Gson()
                try {
                    val wd = gson.fromJson(response, GSONWeather::class.java)
                    val weatherConv = WeatherConvertor()
                    todaysWeather = weatherConv.createWeatherData(wd)
                    updateWeather(todaysWeather)
                    weatherPresent.value = "True"
                    Log.d("Volley", "Got Data ${todaysWeather.weatherDescription}")
                } catch (jse: JsonSyntaxException) {
                    Log.d(
                        "Volley",
                        "com.ryalls.team.gofishing.data.weather.Weather not found $jse"
                    )
                }
            },
            Response.ErrorListener { response ->
                Log.d("Volley", "Didn't work $response")
            })
        queue.add(stringReq)
    }

    /**
     * Gets the address for the last known location.
     */
    @SuppressLint("MissingPermission")
    fun getAddress(
        act: Activity,
        fusedLocationClient: FusedLocationProviderClient?,
        weather: Boolean
    ) {
        val difference = System.currentTimeMillis() - weatherCache
        if (difference < timeToWait) {
            Log.d("WeatherCached", "Weather has been cached for $difference")
            updateWeather(todaysWeather)
            if (isNewRecord()) {
                updateLocation(todaysLocation)
            }
            return
        } else {
            Log.d("WeatherCached", "Weather has been retrieved")
            weatherCache = System.currentTimeMillis()
        }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                fusedLocationClient?.lastLocation?.addOnSuccessListener(
                    act,
                    OnSuccessListener { location ->
                        val town: String? = "Unknown"
                        if (location == null) {
                            Log.w("ViewModel", "onSuccess:null")
                            return@OnSuccessListener
                        }
                        lastLocation = location
                        val gc = Geocoder(act, Locale.getDefault())
                        try {
                            val addresses =
                                gc.getFromLocation(location.latitude, location.longitude, 1)
                            updateLocation(
                                location.latitude.toString(),
                                location.longitude.toString()
                            )
                            if (addresses.size > 0) {
                                val address = addresses[0]
                                todaysLocation = address.getAddressLine(0)
                            }
                        } catch (ioe: IOException) {
                            // if no location then city should be "Unknown"
                        }
                        Log.i("Volley", "Location is = $town")
                        if (weather) {
                            getWeather(act as Context, location)
                        }
                        homeLocationReady.value = "True"
                    })?.addOnFailureListener(act) { e ->
                    Log.w(
                        "Volley",
                        "getLastLocation:onFailure",
                        e
                    )
                }
            }
        }
    }


}


