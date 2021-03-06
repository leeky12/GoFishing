package com.ryalls.team.gofishing.ui.catch_entry

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.ryalls.team.gofishing.R
import com.ryalls.team.gofishing.interfaces.FishingPermissions
import com.ryalls.team.gofishing.interfaces.RequestPerm
import com.ryalls.team.gofishing.persistance.CatchRecord
import kotlinx.android.synthetic.main.app_bar_start_activity.*
import kotlinx.android.synthetic.main.catch_basic.*
import kotlinx.android.synthetic.main.edit_tabbed_fragment.*


class CatchEntryFragment : Fragment() {

    private lateinit var requestPerm: RequestPerm

    private lateinit var fishingPermissions: FishingPermissions

    private var fusedLocationClient: FusedLocationProviderClient? = null

    private val viewModel: CatchDetailsViewModel by activityViewModels()
    private var dbID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        // clear out instances between calls
        viewModel.resetCatchDetails()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
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
        // start the request for weather and location

        // set up the pager fragments here
        view_pager.adapter =
            CatchEntryPagerAdapter(context as Context, childFragmentManager, fishingPermissions)
        tabs.setupWithViewPager(view_pager)

        activity?.fab?.hide()

        dbID = arguments?.getString("dbID")
        if (dbID != null) {
            viewModel.getCatchRecord(dbID!!.toInt())
            viewModel.setNewRecord(false)
        } else {
            if (fishingPermissions.checkFishingPermissions()) {
                viewModel.getAddress(requireActivity(), fusedLocationClient, true)
            }
            viewModel.setNewRecord(true)
        }
    }

    override fun onStart() {
        super.onStart()
        requestPerm.requestPerm()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add_catch -> {

                if (viewModel.homeLocationReady.value.isNullOrEmpty()) {
                    Toast.makeText(
                        activity,
                        "No current location/weather information",
                        Toast.LENGTH_SHORT
                    ).show()
                }

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
        viewModel.insertRecord(catchRecord)
    }

    private fun updateRecord() {
        val catchRecord = getCatchRecord()
        viewModel.updateRecord(catchRecord)
    }

    private fun getCatchRecord(): CatchRecord {
        return viewModel.catchRecord
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            fishingPermissions = context as FishingPermissions
            requestPerm = context as RequestPerm
        } catch (castException: ClassCastException) {
            Log.d("WordPuzzleSolver", "Interface Not Defined")
        }
    }

}