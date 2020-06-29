package com.ryalls.team.gofishing.ui.catch_entry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.ryalls.team.gofishing.R
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
class CurrentCatchLocation : Fragment(), OnMapReadyCallback {
    // TODO: Rename and change types of parameters
    private var latitude: String = ""
    private var longitude: String = ""
    private lateinit var mMap: GoogleMap
    private val viewModel: CatchDetailsViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.fab?.hide()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            latitude = it.getString(ARG_PARAM1).toString()
            longitude = it.getString(ARG_PARAM2).toString()
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
        var lat: Double
        var long: Double
        try {
            lat = java.lang.Double.valueOf(viewModel.catchRecord.latitude)
            long = java.lang.Double.valueOf(viewModel.catchRecord.longitude)
        } catch (nfe: NumberFormatException) {
            lat = 0.0
            long = 0.0
        }
        val marker = mMap.addMarker(
            MarkerOptions()
                .position(
                    LatLng(
                        lat,
                        long
                    )
                )
        )
        mMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    lat,
                    long
                ), 12.0f
            )
        )
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment CatchMap.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            CurrentCatchLocation()
    }
}
