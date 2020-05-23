package com.ryalls.team.gofishing.ui.catch_entry

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import com.ryalls.team.gofishing.R
import com.ryalls.team.gofishing.interfaces.FishingPermissions
import com.ryalls.team.gofishing.persistance.CatchRecord
import kotlinx.android.synthetic.main.app_bar_start_activity.*
import kotlinx.android.synthetic.main.catch_basic.*
import kotlinx.android.synthetic.main.edit_tabbed_fragment.*
import java.io.IOException
import java.util.*


class CatchEntryFragment : Fragment(), FishingPermissions {


    private val TAG = CatchEntryFragment::class.java.simpleName

    private val REQUEST_PERMISSIONS_CODE = 34

    private lateinit var appBarConfiguration: AppBarConfiguration

    /**
     * Represents a geographical location.
     */
    private var lastLocation: Location? = null

    /**
     * Provides access to the Fused Location Provider API.
     */
    private var fusedLocationClient: FusedLocationProviderClient? = null

    private val viewModel: CatchDetailsViewModel by activityViewModels()
    private var dbID: String? = null

    private val permissions =
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        // clear out instances between calls
        viewModel.resetCatchDetails()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity as Activity)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        retainInstance = true
        checkPermission(permissions, REQUEST_PERMISSIONS_CODE)
        return inflater.inflate(R.layout.edit_tabbed_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // set up the pager fragments here
        view_pager.adapter = CatchEntryPagerAdapter(context as Context, childFragmentManager, this)
        tabs.setupWithViewPager(view_pager)

        activity?.fab?.hide()

        dbID = arguments?.getString("dbID")
        if (dbID != null) {
            viewModel.getRecord(dbID!!.toInt())
        }

    }

    override fun onStart() {
        super.onStart()

        if (!checkPermission(permissions, REQUEST_PERMISSIONS_CODE)) {
            requestPermissions(permissions, REQUEST_PERMISSIONS_CODE)
        } else {
            getAddress()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun checkPermission(permissions: Array<String>, requestCode: Int): Boolean {
        // only use for newer versions of android
        if (Build.VERSION.SDK_INT >= 23) {
            return if (ContextCompat.checkSelfPermission(
                    activity as Context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
                    activity as Context,
                    Manifest.permission.CAMERA
                )
                == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
                    activity as Context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                true
            } else {
                // Requesting the permission
                requestPermissions(permissions, requestCode)
                false
            }
        } else {
            return true
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )

        // check you have permissions to get the gps lat/long coordinated from the system
        // if you do then get the location and weather information
        if (checkPermission(permissions, REQUEST_PERMISSIONS_CODE)) {
            getAddress()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add_catch -> {
                // check to see if the catch has been filled in, at least trout
                Log.d("Current Page", "Current Page " + view_pager.currentItem)

                val tag = "android:switcher:" + R.id.view_pager + ":" + view_pager.currentItem
                val myFragment = childFragmentManager.findFragmentByTag(tag)

                myFragment?.onPause()

                if (viewModel.catchRecord.species.isEmpty()) {
                    view_pager.setCurrentItem(1, true)
                    myFragment?.speciesField?.error = getString(R.string.enter_species)
                    return true
                }

                if (dbID == null) {
                    insertRecord()
                } else {
                    updateRecord()
                }
                // pop back the stack so you go back to the list view
                val navController = findNavController().popBackStack()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun insertRecord() {
        val catchRecord = getCatchRecord()
        viewModel.insert(catchRecord)
    }

    private fun updateRecord() {
        val catchRecord = getCatchRecord()
        viewModel.update(catchRecord)
    }

    private fun getCatchRecord(): CatchRecord {
        return viewModel.catchRecord
    }

    /**
     * Gets the address for the last known location.
     */
    @SuppressLint("MissingPermission")
    private fun getAddress() {
        fusedLocationClient?.lastLocation?.addOnSuccessListener(
            activity as Activity,
            OnSuccessListener { location ->
                val town: String? = "Unknown"
                if (location == null) {
                    Log.w(TAG, "onSuccess:null")
                    return@OnSuccessListener
                }

                lastLocation = location
                val gc = Geocoder(requireActivity(), Locale.getDefault())
                val address: Address
                try {
                    val addresses =
                        gc.getFromLocation(location.latitude, location.longitude, 1)
                    val sb = StringBuilder()
                    if (addresses.size > 0) {
                        address = addresses[0]
//                    town = address.locality
                    }
                } catch (ioe: IOException) {
                    // if no location then city should be "Unknown"
                }

                // put some code in so this goes to a temporary structure and of its a new entry
                // then copy this structure over to the catchRecord else leave the original record alone
                // as its a viewable or edit on that record

                if (town != null) {
//                viewModel.catchRecord.location = town
                }
                Log.i(TAG, "Location is = $town")

                viewModel.getWeather(requireContext(), location)

            })?.addOnFailureListener(requireActivity()) { e ->
            Log.w(
                TAG,
                "getLastLocation:onFailure",
                e
            )
        }
    }

    override fun checkFishingPermissions(): Boolean {
        return checkPermission(permissions, REQUEST_PERMISSIONS_CODE)
    }

}