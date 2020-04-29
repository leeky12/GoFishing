package com.ryalls.team.gofishing.ui.catch_entry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.ryalls.team.gofishing.R
import kotlinx.android.synthetic.main.catch_details.*

/**
 * A placeholder fragment containing a simple view.
 */
class CatchPicture : Fragment() {


    private val viewModel: CatchDetailsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.catch_picture, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
 //       val data = viewModel.allWords
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
        fun newInstance(sectionNumber: Int): CatchPicture {
            return CatchPicture()
                .apply {
                    arguments = Bundle().apply {
                        putInt(ARG_SECTION_NUMBER, sectionNumber)
                    }
                }
        }
    }
}