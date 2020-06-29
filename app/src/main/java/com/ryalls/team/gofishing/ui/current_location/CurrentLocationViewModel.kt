package com.ryalls.team.gofishing.ui.current_location

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.ryalls.team.gofishing.persistance.CatchRepository
import com.ryalls.team.gofishing.persistance.CatchRoomDatabase
import com.ryalls.team.gofishing.utils.MapStatus
import java.util.*

class CurrentLocationViewModel(application: Application) : AndroidViewModel(application) {
    private var repository: CatchRepository

    val mapStatus: MutableLiveData<MapStatus> by lazy {
        MutableLiveData<MapStatus>()
    }

    // Mutable String used to indicate the catch locations list has been completely filled  with the data
    val homeLocationReady: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    /**
     * Represents a geographical location.
     */
    var lastLocation: Location? = null
    var currentLocation = ""

    init {
        val catchDao =
            CatchRoomDatabase.getDatabase(context = application, scope = viewModelScope).catchDao()
        repository = CatchRepository(catchDao)
    }

    /**
     * Gets the address for the last known location.
     */
    @SuppressLint("MissingPermission")
    fun getLocation(
        act: Activity,
        fusedLocationClient: FusedLocationProviderClient?
    ) {
        fusedLocationClient?.lastLocation?.addOnSuccessListener(
            act
        ) { location ->
            if (location != null) {
                val gc = Geocoder(act, Locale.getDefault())
                try {
                    val addresses =
                        gc.getFromLocation(location.latitude, location.longitude, 1)
                    if (addresses.size > 0) {
                        val address = addresses[0]
                        currentLocation = address.getAddressLine(0)
                    }
                } catch (e: Exception) {
                    // if no location then city should be "Unknown"
                    currentLocation = "Unknown"
                }
                lastLocation = location
                homeLocationReady.value = "True"
            } else {
                mapStatus.value = MapStatus.NoMAP
                currentLocation = "Unknown"
                homeLocationReady.value = "True"
            }
        }?.addOnFailureListener(act) { e ->
            Log.w(
                "Volley",
                "getLastLocation:onFailure",
                e
            )
            mapStatus.value = MapStatus.NoMAP
            currentLocation = "Unknown"
            homeLocationReady.value = "True"
        }
    }

}