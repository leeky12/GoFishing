package com.ryalls.team.gofishing.ui.catch_entry

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class CatchEntryPagerAdapter(entity: CatchEntryFragment) : FragmentStateAdapter(entity) {


    override fun createFragment(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
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

    override fun getItemCount(): Int {
        return 6
    }
}