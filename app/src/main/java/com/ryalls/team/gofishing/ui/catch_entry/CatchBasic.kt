package com.ryalls.team.gofishing.ui.catch_entry

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.ryalls.team.gofishing.R
import com.ryalls.team.gofishing.data.FishList
import kotlinx.android.synthetic.main.catch_basic.*


/**
 * A placeholder fragment containing a simple view.
 */
class CatchBasic : Fragment() {

    private val viewModel: CatchDetailsViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onPause() {
        super.onPause()
        viewModel.updatesBasicCatch(species = speciesField.text.toString(),
            comment = commentsField.text.toString(),
            weight = weightField.text.toString(),
            length = lengthField.text.toString()
        )
        speciesField.error = null
    }

    override fun onResume() {
        super.onResume()
        if (speciesField.text.toString().isEmpty())
        {
            speciesField.error = getString(R.string.enter_species)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.catch_basic, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

         val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(context as Context, android.R.layout.select_dialog_item, FishList.fish_list)

        speciesField.threshold = 1
        speciesField.setAdapter(adapter)

        viewModel.recordReady.observe(viewLifecycleOwner, Observer { catch ->
            weightField.setText(viewModel.catchRecord.weight)
            lengthField.setText(viewModel.catchRecord.length)
            commentsField.setText(viewModel.catchRecord.comments)
            speciesField.setText(viewModel.catchRecord.species)
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