package com.ryalls.team.gofishing

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.ryalls.team.gofishing.interfaces.FishingPermissions
import com.ryalls.team.gofishing.interfaces.RequestPerm
import com.ryalls.team.gofishing.ui.catch_entry.CatchDetailsViewModel


class FishingActivity : AppCompatActivity(), FishingPermissions, RequestPerm {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private val requestPermissionsCode = 34
    private val permissions =
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

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
                R.id.nav_view, R.id.nav_current_location, R.id.nav_map
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            val catchDetailsViewModel =
                ViewModelProvider(this).get(CatchDetailsViewModel::class.java)
            catchDetailsViewModel.setNewRecord(true)
            catchDetailsViewModel.resetCatchDetails()
            navController.navigate(R.id.nav_details)
        }
    }

    override fun requestPerm() {
        if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(this, permissions, requestPermissionsCode)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun checkPermission(permissions: Array<String>, requestCode: Int): Boolean {
        // only use for newer versions of android
        if (Build.VERSION.SDK_INT >= 23) {
            return (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
                    == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            )
                    == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
                    == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
                    == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
                    == PackageManager.PERMISSION_GRANTED)
        }
        return true
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
        if (checkPermission(permissions, requestPermissionsCode)) {
            // start the request for weather and location
//            viewModel.getAddress(this, fusedLocationClient, true)
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.CAMERA
            )
        ) {
            //Show permission explanation dialog...
        } else {
            //Never ask again selected, or device policy prohibits the app from having that permission.
            //So, disable that feature, or fall back to another situation...
        }

    }

    override fun checkFishingPermissions(): Boolean {
        return checkPermission(permissions, requestPermissionsCode)
    }


}
