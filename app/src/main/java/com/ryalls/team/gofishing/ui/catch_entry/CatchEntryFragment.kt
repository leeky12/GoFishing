package com.ryalls.team.gofishing.ui.catch_entry

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.ryalls.team.gofishing.BuildConfig
import com.ryalls.team.gofishing.R
import com.ryalls.team.gofishing.persistance.CatchRecord
import kotlinx.android.synthetic.main.app_bar_start_activity.*
import kotlinx.android.synthetic.main.catch_basic.*
import kotlinx.android.synthetic.main.edit_tabbed_fragment.*
import java.io.IOException
import java.util.*


class CatchEntryFragment : Fragment() {


    private val TAG = CatchEntryFragment::class.java.simpleName

    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity as Activity)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        retainInstance = true
        return inflater.inflate(R.layout.edit_tabbed_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        view_pager.adapter = CatchEntryPagerAdapter(context as Context, childFragmentManager)
        tabs.setupWithViewPager(view_pager)

        activity?.fab?.hide()

        dbID = arguments?.getString("dbID")
        if (dbID != null) {
            viewModel.getRecord(dbID!!.toInt())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
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

    override fun onStart() {
        super.onStart()

        if (!checkPermissions()) {
            requestPermissions()
        } else {
            getAddress()
        }
    }

    /**
     * Gets the address for the last known location.
     */
    @SuppressLint("MissingPermission")
    private fun getAddress() {
        fusedLocationClient?.lastLocation?.addOnSuccessListener(
            activity as Activity,
            OnSuccessListener { location ->
                var town: String? = "Unknown"
                if (location == null) {
                    Log.w(TAG, "onSuccess:null")
                    return@OnSuccessListener
                }

                lastLocation = location
                val gc = Geocoder(activity as Activity, Locale.getDefault())
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

                viewModel.getWeather(context as Context, location)


            })?.addOnFailureListener(activity as Activity) { e ->
            Log.w(
                TAG,
                "getLastLocation:onFailure",
                e
            )
        }
    }

    /**
     * Return the current state of the permissions needed.
     */
    private fun checkPermissions(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(
            activity as Activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            activity as Activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.")

            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                View.OnClickListener {
                    // Request permission
                    ActivityCompat.requestPermissions(
                        activity as Activity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_PERMISSIONS_REQUEST_CODE
                    )
                })

        } else {
            Log.i(TAG, "Requesting permission")
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    /**
     * Shows a [Snackbar].
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private fun showSnackbar(
        mainTextStringId: Int,
        actionStringId: Int,
        listener: View.OnClickListener
    ) {
        Snackbar.make(
            view as View, getString(mainTextStringId),
            Snackbar.LENGTH_INDEFINITE
        )
            .setAction(getString(actionStringId), listener)
            .show()
    }


    /**
     * Callback received when a permissions request has been completed.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        Log.i(TAG, "onRequestPermissionResult")

        if (requestCode != REQUEST_PERMISSIONS_REQUEST_CODE) return

        when {
            grantResults.isEmpty() ->
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.")
            grantResults[0] == PackageManager.PERMISSION_GRANTED -> // Permission granted.
                getAddress()
            else -> // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.

                showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                    View.OnClickListener {
                        // Build intent that displays the App settings screen.
                        val intent = Intent().apply {
                            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        startActivity(intent)
                    })
        }

    }


}