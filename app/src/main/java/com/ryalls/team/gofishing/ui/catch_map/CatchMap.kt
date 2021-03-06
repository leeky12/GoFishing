package com.ryalls.team.gofishing.ui.catch_map

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.ryalls.team.gofishing.R
import com.ryalls.team.gofishing.persistance.MapData
import com.ryalls.team.gofishing.utils.MapStatus
import kotlinx.android.synthetic.main.app_bar_start_activity.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CatchMap.newInstance] factory method to
 * create an instance of this fragment.
 */
class CatchMap : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var mMap: GoogleMap


    private var fusedLocationClient: FusedLocationProviderClient? = null
    private val viewModel: MapViewModel by activityViewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.fab?.hide()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        retainInstance = true
        val root = inflater.inflate(R.layout.catch_map, container, false)
        val fm = childFragmentManager

        // set up the initial location, layout etc. before the map appears
        val options = GoogleMapOptions()
            .zoomControlsEnabled(true)
            .compassEnabled(true)
            .mapType(GoogleMap.MAP_TYPE_HYBRID)

        val fragment = SupportMapFragment.newInstance(options)
        fm.beginTransaction().replace(R.id.map, fragment).commit()
        fragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        return root
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (viewModel.lastLocation == null) {
            // have an observer on this to tell me when an address is present
            viewModel.getMapAddress(requireActivity(), fusedLocationClient)
        }

        viewModel.mapStatus.observe(viewLifecycleOwner, Observer {
            when (it) {
                MapStatus.NoMAP -> Toast.makeText(
                    activity,
                    "No current location information available",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        viewModel.homeLocationReady.observe(viewLifecycleOwner, Observer {
            // using elvis to use a default value if the data in the view model is null
            val lat = viewModel.lastLocation?.latitude ?: 51.2
            val long = viewModel.lastLocation?.longitude ?: -1.14
            val latLng = LatLng(lat, long)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f))
        })

        // I have an observer on this to tell me when all the catch locations are present to be displayed
        viewModel.getCatchLocations()
        viewModel.catchLocationsReady.observe(viewLifecycleOwner, Observer {
            updateMap(viewModel.catchLocations)
        })
        mMap.setOnMarkerClickListener(this)
    }

    private fun updateMap(fishedList: List<MapData>?) {
        if (fishedList != null) {
            for (fish in fishedList) {
                if (fish.latitude != null) {
                    var lat = 0.0
                    var long = 0.0
                    try {
                        lat = java.lang.Double.valueOf(fish.latitude)
                        long = java.lang.Double.valueOf(fish.longitude)
                    } catch (nfe: NumberFormatException) {
                        continue
                    }
                    val marker = mMap.addMarker(
                        MarkerOptions()
                            .position(
                                LatLng(
                                    lat,
                                    long
                                )
                            )
                            .snippet(fish.species)
                            .title(fish.date)
                    )
                    marker.tag = fish.catchID
                }
            }
        }
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        Log.d("CatchMap", "" + marker?.tag)
        return false
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CatchMap.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CatchMap().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
