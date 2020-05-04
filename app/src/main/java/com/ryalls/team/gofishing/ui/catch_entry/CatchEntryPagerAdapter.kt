package com.ryalls.team.gofishing.ui.catch_entry

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ryalls.team.gofishing.R

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class CatchEntryPagerAdapter(private val context: Context, fm: FragmentManager)
    : FragmentPagerAdapter(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val TAB_TITLES = arrayOf(
        R.string.catch_picture,
        R.string.catch_basic,
        R.string.catch_details,
        R.string.catch_tackle,
        R.string.tab_text_4,
        R.string.tab_text_5
    )

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 ->
                CatchPicture.newInstance(position + 1)
            1 ->
                CatchBasic.newInstance(position + 1)
            2 ->
                CatchDetails.newInstance(position + 1)
            3 ->
                CatchTackle.newInstance(position + 1)
            4 ->
                Fragment4.newInstance(position + 1)
            else ->
                Fragment5.newInstance(position + 1)
        }
    }

    override fun getCount(): Int {
        return 6
    }
}