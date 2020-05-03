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
import kotlinx.android.synthetic.main.catch_basic.*

/**
 * A placeholder fragment containing a simple view.
 */
class CatchBasic : Fragment() {

    override fun onResume() {
        super.onResume()
    }

    private val viewModel: CatchDetailsViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onPause() {
        super.onPause()
        Log.d("CatchBasic", "Being Paused")
        viewModel.updatesBasicCatch(species = speciesField.text.toString(),
            comment = commentsField.text.toString(),
            weight = weightField.text.toString(),
            length = lengthField.text.toString()
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.catch_basic, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // in the foreground.
        viewModel.recordReady.observe(viewLifecycleOwner, Observer { catch ->
            speciesField.setText(viewModel.catchRecord?.species)
            weightField.setText(viewModel.catchRecord?.weight)
            lengthField.setText(viewModel.catchRecord?.length)
            commentsField.setText(viewModel.catchRecord?.comments)
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
        fun newInstance(sectionNumber: Int): CatchBasic {
            return CatchBasic()
                .apply {
                    arguments = Bundle().apply {
                        putInt(ARG_SECTION_NUMBER, sectionNumber)
                    }
                }
        }
    }
}