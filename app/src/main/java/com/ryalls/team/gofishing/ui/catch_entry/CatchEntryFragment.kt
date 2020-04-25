package com.ryalls.team.gofishing.ui.catch_entry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.ryalls.team.gofishing.R
import kotlinx.android.synthetic.main.edit_tabbed_fragment.*

class CatchEntryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        return inflater.inflate(R.layout.edit_tabbed_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val TAB_TITLES = arrayOf(
            R.string.catch_picture,
            R.string.catch_basic,
            R.string.catch_details,
            R.string.catch_tackle,
            R.string.tab_text_4,
            R.string.tab_text_5
        )

        view_pager.adapter = CatchEntryPagerAdapter(this)
        TabLayoutMediator(tabs, view_pager,
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                tab.text = getString(TAB_TITLES[position])
            }).attach()
    }

}