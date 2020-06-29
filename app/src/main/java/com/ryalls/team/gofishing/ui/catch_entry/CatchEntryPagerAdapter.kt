package com.ryalls.team.gofishing.ui.catch_entry

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.ryalls.team.gofishing.R
import com.ryalls.team.gofishing.interfaces.FishingPermissions

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class CatchEntryPagerAdapter(
    private val context: Context,
    fm: FragmentManager,
    parentFragment: FishingPermissions
)
    : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private var permFragment: FishingPermissions = parentFragment

    private val TABTITLES = arrayOf(
        R.string.catch_picture,
        R.string.catch_basic,
        R.string.catch_details,
        R.string.catch_tackle,
        R.string.tab_text_4,
        R.string.catch_location
    )

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TABTITLES[position])
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 ->
                CatchPicture.newInstance(permFragment)
            1 ->
                CatchBasic.newInstance(1)
            2 ->
                CatchDetails.newInstance(2)
            3 ->
                CatchTackle.newInstance(3)
            4 ->
                CatchWeather.newInstance(4)
            else ->
                CurrentCatchLocation.newInstance()


        }
    }

    override fun getCount(): Int {
        return 6
    }
}