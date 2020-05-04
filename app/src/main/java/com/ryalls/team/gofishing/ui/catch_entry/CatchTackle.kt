package com.ryalls.team.gofishing.ui.catch_entry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.ryalls.team.gofishing.R
import kotlinx.android.synthetic.main.catch_tackle.*

/**
 * A placeholder fragment containing a simple view.
 */
class CatchTackle : Fragment() {

    private val viewModel: CatchDetailsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        retainInstance = true
        return inflater.inflate(R.layout.catch_tackle, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.recordReady.observe(viewLifecycleOwner, Observer { catch ->
            rodField.setText(viewModel.catchRecord.rod)
            reelField.setText(viewModel.catchRecord.reel)
            lineField.setText(viewModel.catchRecord.line)
        })
    }

    override fun onPause() {
        super.onPause()
        viewModel.updatesTackle(
            rod = rodField.text.toString(),
            line = lineField.text.toString(),
            reel = reelField.text.toString()
        )
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
        fun newInstance(sectionNumber: Int): CatchTackle {
            return CatchTackle().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}