package com.ryalls.team.gofishing.ui.current_location

import android.os.Bundle
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
import com.google.android.gms.maps.model.MarkerOptions
import com.ryalls.team.gofishing.R
import com.ryalls.team.gofishing.utils.MapStatus
import kotlinx.android.synthetic.main.app_bar_start_activity.*
import kotlinx.android.synthetic.main.catch_location.*

/**
 * A simple [Fragment] subclass.
 * Use the [CurrentLocation.newInstance] factory method to
 * create an instance of this fragment.
 */
class CurrentLocation : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val viewModel: CurrentLocationViewModel by activityViewModels()
    private var fusedLocationClient: FusedLocationProviderClient? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.catch_location, container, false)
        val fm = childFragmentManager

        // set up the initial location, layout etc. before the map appears
        val options = GoogleMapOptions()
            .zoomControlsEnabled(true)
            .compassEnabled(true)
            .mapType(GoogleMap.MAP_TYPE_HYBRID)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val fragment = SupportMapFragment.newInstance(options)
        fm.beginTransaction().replace(R.id.locationmap, fragment).commit()
        fragment.getMapAsync(this)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.fab?.hide()
        viewModel.getLocation(requireActivity(), fusedLocationClient)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        viewModel.homeLocationReady.observe(viewLifecycleOwner, Observer {
            // using elvis to use a default value if the data in the view model is null
            val lat = viewModel.lastLocation?.latitude ?: 0.0
            val long = viewModel.lastLocation?.longitude ?: 0.0
            val latLng = LatLng(lat, long)
            currentLocation.setText(viewModel.currentLocation)
            currentLatitude.setText("" + lat)
            currentLongtitude.setText("" + long)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f))
            val marker = mMap.addMarker(
                MarkerOptions()
                    .position(
                        LatLng(
                            lat,
                            long
                        )
                    )
                    .snippet(viewModel.currentLocation)
                    .title("Current Location")
            )
            if (lat != 0.0 && long != 0.0) {
                marker.showInfoWindow()
            }
        })
        viewModel.mapStatus.observe(viewLifecycleOwner, Observer {
            when (it) {
                MapStatus.NoMAP -> Toast.makeText(
                    activity,
                    "No current location information available",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BlankFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CurrentLocation()
    }
}