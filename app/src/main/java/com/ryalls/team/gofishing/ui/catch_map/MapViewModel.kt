package com.ryalls.team.gofishing.ui.catch_map

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.ryalls.team.gofishing.persistance.CatchRepository
import com.ryalls.team.gofishing.persistance.CatchRoomDatabase
import com.ryalls.team.gofishing.persistance.MapData
import com.ryalls.team.gofishing.utils.MapStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapViewModel(application: Application) : AndroidViewModel(application) {
    private var repository: CatchRepository
    lateinit var catchLocations: List<MapData>
    val mapStatus: MutableLiveData<MapStatus> by lazy {
        MutableLiveData<MapStatus>()
    }

    // Mutable String used to indicate the catch locations list has been completely filled  with the data
    val catchLocationsReady: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    // Mutable String used to indicate the catch locations list has been completely filled  with the data
    val homeLocationReady: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    /**
     * Represents a geographical location.
     */
    var lastLocation: Location? = null

    init {
        val catchDao =
            CatchRoomDatabase.getDatabase(context = application, scope = viewModelScope).catchDao()
        repository = CatchRepository(catchDao)
    }

    fun getCatchLocations() {
        val d = Log.d("MapView Coroutine", "Started")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                catchLocations = repository.getCatchLocations()
                Log.d("MapView", "Finished")
            }
            withContext(Dispatchers.Main) {
                Log.d("MapView", "Catch List retrieved")
                catchLocationsReady.value = "True"
            }
        }
    }

    /**
     * Gets the address for the last known location.
     */
    @SuppressLint("MissingPermission")
    fun getMapAddress(
        act: Activity,
        fusedLocationClient: FusedLocationProviderClient?
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                fusedLocationClient?.lastLocation?.addOnSuccessListener(
                    act
                ) { location ->
                    if (location != null) {
                        lastLocation = location
                        homeLocationReady.value = "True"
                    } else {
                        mapStatus.value = MapStatus.No_MAP
                    }
                }?.addOnFailureListener(act) { e ->
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