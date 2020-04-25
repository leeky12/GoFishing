package com.ryalls.team.gofishing.ui.catch_entry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.ryalls.team.gofishing.R
import com.ryalls.team.gofishing.data.CatchData
import kotlinx.android.synthetic.main.catch_details.*

/**
 * A placeholder fragment containing a simple view.
 */
class CatchDetails : Fragment() {

    private val viewModel: CatchDetailsViewModel by  activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

       return inflater.inflate(R.layout.catch_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val catch = viewModel.catchReady.value
        if (catch != null) {
            lureText.setText(catch.dbId)
        }
    }

        private fun catchUpdated() {
        lureText.setText("data set")
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