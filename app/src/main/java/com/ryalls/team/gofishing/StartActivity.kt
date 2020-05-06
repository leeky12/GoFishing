package com.ryalls.team.gofishing

import android.Manifest
import android.annotation.SuppressLint
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
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.ryalls.team.gofishing.ui.catch_entry.CatchDetailsViewModel
import java.io.IOException
import java.util.*


class StartActivity : AppCompatActivity() {

    private val TAG = StartActivity::class.java.simpleName

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


    private val viewModel: CatchDetailsViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_activity)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        // R.id.nav_details,
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_view, R.id.nav_settings, R.id.nav_map
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            val catchDetailsViewModel = ViewModelProvider(this).get(CatchDetailsViewModel::class.java)
            catchDetailsViewModel.resetCatchDetails()
            navController.navigate(R.id.nav_details)
        }

       fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


    }

    public override fun onStart() {
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
        fusedLocationClient?.lastLocation?.addOnSuccessListener(this, OnSuccessListener { location ->
            var town: String? = "Unknown"
            if (location == null) {
                Log.w(TAG, "onSuccess:null")
                return@OnSuccessListener
            }

            lastLocation = location
            val gc = Geocoder(this@StartActivity, Locale.getDefault())
            val address: Address
             try {
                val addresses =
                    gc.getFromLocation(location.latitude, location.longitude, 1)
                val sb = StringBuilder()
                if (addresses.size > 0) {
                    address = addresses[0]
                    town = address.getAddressLine(0)
                }
            } catch (ioe: IOException) {
                // if no location then city should be "Unknown"
            }

            // put some code in so this goes to a temporary structure and of its a new entry
            // then copy this structure over to the catchRecord else leave the original record alone
            // as its a viewable or edit on that record

            if (town != null) {
                viewModel.catchRecord.location = town
            }

            viewModel.getWeather(application as Context, location)


        })?.addOnFailureListener(this) { e -> Log.w(TAG, "getLastLocation:onFailure", e) }
    }


    /**
     * Return the current state of the permissions needed.
     */
    private fun checkPermissions(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION)
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this,
            Manifest.permission.ACCESS_FINE_LOCATION)

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.")

            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                View.OnClickListener {
                    // Request permission
                    ActivityCompat.requestPermissions(this@StartActivity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_PERMISSIONS_REQUEST_CODE)
                })

        } else {
            Log.i(TAG, "Requesting permission")
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(this@StartActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_PERMISSIONS_REQUEST_CODE)
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
        Snackbar.make(findViewById(android.R.id.content), getString(mainTextStringId),
            Snackbar.LENGTH_INDEFINITE)
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



    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
