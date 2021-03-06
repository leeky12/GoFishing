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
import com.ryalls.team.gofishing.data.SpeciesCount
import com.ryalls.team.gofishing.data.WeatherData
import com.ryalls.team.gofishing.data.weather.GSONWeather
import com.ryalls.team.gofishing.persistance.CatchRecord
import com.ryalls.team.gofishing.persistance.CatchRepository
import com.ryalls.team.gofishing.persistance.CatchRoomDatabase
import com.ryalls.team.gofishing.utils.Thumbnail
import com.ryalls.team.gofishing.utils.WeatherConvertor
import com.ryalls.team.gofishing.utils.WeatherStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class CatchDetailsViewModel(application: Application) : AndroidViewModel(application) {
    val weatherStatus: MutableLiveData<WeatherStatus> by lazy {
        MutableLiveData<WeatherStatus>()
    }

    // 1hr in milliseconds
    private val timeToWait: Long = 3600000

    private var repository: CatchRepository
    private var weatherCache: Long = 0L
    private var todaysLocation = ""

    private var lastLocation: Location? = null

    private var isNewRecord = false

    var allWords: LiveData<List<CatchRecord>>

    var todaysWeather: WeatherData = WeatherData
    private lateinit var hashMap: HashMap<String, Int>

    val weatherPresent: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    // Mutable String used to indicate the home location has been found
    val homeLocationReady: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    // Mutable String used to indicate the fishcount has been calculated
    val fishCountReady: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
    var speciesCount = ArrayList<SpeciesCount>()


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
        catchRecord = CatchRecord("")

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
//        updateRecord.catchID = catchRecord.catchID
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
        repository.insert(catchRecord)
    }

    private fun returnBlankIfZero(value: String): String {
        return if (value == "0.0") {
            ""
        } else {
            value
        }
    }

    fun calculateCatch() {
        var fishList: List<String>
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                fishList = repository.getSpeciesList()
                hashMap = HashMap<String, Int>(30)
                for (item in fishList) {
                    var value = hashMap[item]
                    if (value == null) {
                        hashMap[item] = 1
                    } else {
                        value++
                        hashMap[item] = value
                    }
                }
                withContext(Dispatchers.Main) {
                    hashMap.forEach { (key, value) ->
                        val entry = SpeciesCount()
                        entry.species = key
                        entry.count = value
                        speciesCount.add(entry)
                        Log.d("Info", entry.species)
                    }
                    for (species in speciesCount) {
                        Log.d("InInfoModel", species.species + " " + species.count)
                    }
                    fishCountReady.value = true
                }
            }
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
                    // if I was unable to goecode the location from google then get the location from
                    // the weather API, which is sometimes nicer
                    if (todaysLocation.isEmpty()) {
                        todaysLocation = wd.name
                    }
                    weatherPresent.value = "True"
                    Log.d("Volley", "Got Data ${todaysWeather.weatherDescription}")
                } catch (jse: JsonSyntaxException) {
                    weatherStatus.value = WeatherStatus.NoWEATHER
                    Log.d(
                        "Volley",
                        "com.ryalls.team.gofishing.data.weather.Weather not found $jse"
                    )
                }
            },
            Response.ErrorListener { response ->
                weatherStatus.value = WeatherStatus.NoWEATHER
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
        if (difference < timeToWait && homeLocationReady.value == "True") {
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
        fusedLocationClient?.lastLocation?.addOnSuccessListener(
            act,
            OnSuccessListener { location ->
                val town: String? = "Unknown"
                if (location == null) {
                    Log.w("ViewModel", "onSuccess:null")
                    weatherStatus.value = WeatherStatus.NoLOCATION
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
                        updateLocation(todaysLocation)
                    }
                } catch (ioe: IOException) {
                    weatherStatus.value = WeatherStatus.NoLOCATION
                    // if no location then city should be "Unknown"
                }
                Log.i("Volley", "Location is = $town")
                if (weather) {
                    getWeather(act as Context, location)
                }
                homeLocationReady.value = "True"
            })?.addOnFailureListener(act) { e ->
            weatherStatus.value = WeatherStatus.NoLOCATION
            Log.w(
                "Volley",
                "getLastLocation:onFailure",
                e
            )
        }
    }

}


