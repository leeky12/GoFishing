package com.ryalls.team.gofishing.ui.catch_entry

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.ryalls.team.gofishing.R
import kotlinx.android.synthetic.main.catch_details.*

/**
 * A placeholder fragment containing a simple view.
 */
class CatchDetails : Fragment() {

    private val viewModel: CatchDetailsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        retainInstance = true
        return inflater.inflate(R.layout.catch_details, container, false)
    }

    override fun onPause() {
        super.onPause()
        Log.d("CatchBasic", "Being Paused")
        viewModel.updatesDetailsCatch(
            lure = lureField.text.toString(),
            structure = structureField.text.toString(),
            conditions = water_conditionsField.text.toString(),
            depth = fish_depthField.text.toString(),
            hook = hook_sizeField.text.toString(),
            groundbait = ground_baitField.text.toString(),
            boatspeed = boat_speedField.text.toString(),
            tides = tidesField.text.toString()
        )
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.recordReady.observe(viewLifecycleOwner, Observer { catch ->
            lureField.setText(viewModel.catchRecord?.lure)
            structureField.setText(viewModel.catchRecord?.structure)
            water_conditionsField.setText(viewModel.catchRecord?.conditions)
            fish_depthField.setText(viewModel.catchRecord?.depth)

            hook_sizeField.setText(viewModel.catchRecord?.hook)
            ground_baitField.setText(viewModel.catchRecord?.groundBait)
            boat_speedField.setText(viewModel.catchRecord?.boatspeed)
            tidesField.setText(viewModel.catchRecord?.tides)
        })
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(sectionNumber: Int): CatchDetails {
            return CatchDetails().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}