package com.ryalls.team.gofishing.ui.catch_map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.ryalls.team.gofishing.R
import com.ryalls.team.gofishing.persistance.CatchRecord
import com.ryalls.team.gofishing.ui.catch_entry.CatchDetailsViewModel
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
class CatchMap : Fragment(), OnMapReadyCallback {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var mMap: GoogleMap

    private val viewModel: CatchDetailsViewModel by activityViewModels()
    private var fusedLocationClient: FusedLocationProviderClient? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.fab?.hide()
    }

    private val viewModels: CatchDetailsViewModel by activityViewModels()


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
        val root = inflater.inflate(R.layout.catch_map, container, false)
        val fm = childFragmentManager

        // set up the initial location, layout etc. before the map appears
        val options = GoogleMapOptions()
            .camera(
                CameraPosition.fromLatLngZoom(
                    LatLng(51.264116, -1.1068298), 14.0f
                )
            )
            .zoomControlsEnabled(true)
            .compassEnabled(true)
            .mapType(GoogleMap.MAP_TYPE_HYBRID)

        val fragment = SupportMapFragment.newInstance(options)
        fm.beginTransaction().replace(R.id.map, fragment).commit()
        fragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        viewModel.catchLocationsReady.observe(viewLifecycleOwner, Observer { locations ->
            updateMap(viewModel.catchLocations)
        })

        viewModel.homeLocationReady.observe(viewLifecycleOwner, Observer { location ->
            val lat = viewModel.lastLocation!!.latitude
            val long = viewModel.lastLocation!!.longitude

            val latLng = LatLng(lat, long)
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        })

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
            viewModel.getAddress(requireActivity(), fusedLocationClient, false)
        }
        val data = viewModels.getCatchLocations()
    }

    private fun updateMap(fishedList: List<CatchRecord>?) {
        if (fishedList != null) {
            for (fish in fishedList) {
                if (fish.latitude != null) {
                    //                   val dateString: String = ConvertDate.getConvertedDateTime(fish.getDate())
                    mMap.addMarker(
                        MarkerOptions()
                            .position(
                                LatLng(
                                    java.lang.Double.valueOf(fish.latitude),
                                    java.lang.Double.valueOf(fish.longitude)
                                )
                            )
                            .snippet(fish.species)
                            .title(fish.date)
                    )
                }
            }
        }
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
