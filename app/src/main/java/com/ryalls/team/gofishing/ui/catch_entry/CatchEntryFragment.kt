package com.ryalls.team.gofishing.ui.catch_entry

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.ryalls.team.gofishing.R
import com.ryalls.team.gofishing.persistance.CatchRecord
import kotlinx.android.synthetic.main.catch_basic.*
import kotlinx.android.synthetic.main.catch_details.*
import kotlinx.android.synthetic.main.catch_tackle.*
import kotlinx.android.synthetic.main.edit_tabbed_fragment.*
import java.text.SimpleDateFormat
import java.util.*

class CatchEntryFragment : Fragment() {

    private val viewModel: CatchDetailsViewModel by activityViewModels()
    private var dbID : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        retainInstance = true
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

        dbID = arguments?.getString("dbID")
        if (dbID != null) {
            viewModel.getRecord(dbID!!.toInt())
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add_catch -> {
                // check to see if the catch has been filled in, at least trout
                val myFragment = childFragmentManager.findFragmentByTag("f" + 1)
                if (myFragment == null) {
                    view_pager.setCurrentItem(1, true)
                    return true
                }
                val data = myFragment.speciesField?.text.toString()
                if (data.isEmpty()) {
                    myFragment.speciesField?.error = "Please enter details"
                    view_pager.setCurrentItem(1, true)
                    return true
                }
                if (dbID == null) {
                    insertRecord()
                } else {
                    updateRecord()
                }
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun insertRecord() {
        val catchRecord = getCatchRecord()
        viewModel.insert(catchRecord)
    }

    private fun updateRecord() {
        val catchRecord = getCatchRecord()
        viewModel.update(catchRecord)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
    }

    private fun getCatchRecord(): CatchRecord {
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        val date = Date()
        // Basic catch page
        val basicPage = childFragmentManager.findFragmentByTag("f" + 1)
        val catchRecord = CatchRecord(
            species = basicPage?.speciesField?.text.toString()
        )

        catchRecord.weight = basicPage?.weightField?.text.toString()
        catchRecord.length = basicPage?.lengthField?.text.toString()
        catchRecord.comments = basicPage?.commentsField?.text.toString()

        // details page
        val detailsPage = childFragmentManager.findFragmentByTag("f" + 2)
        if (detailsPage == null) {
            catchRecord.lure = ""
            catchRecord.structure = ""
            catchRecord.conditions = ""
            catchRecord.depth = ""
            catchRecord.hook = ""
            catchRecord.groundBait = ""
            catchRecord.boatspeed = ""
            catchRecord.tides = ""
        } else {
            catchRecord.lure = detailsPage.lureField.text.toString()
            catchRecord.structure = detailsPage.structureField.text.toString()
            catchRecord.conditions = detailsPage.water_conditionsField.text.toString()
            catchRecord.depth = detailsPage.fish_depthField.text.toString()
            catchRecord.hook = detailsPage.hook_sizeField.text.toString()
            catchRecord.groundBait = detailsPage.ground_baitField.text.toString()
            catchRecord.boatspeed = detailsPage.boat_speedField.text.toString()
            catchRecord.tides = detailsPage.tidesField.text.toString()
        }

        // tackle page
        // details page
        val tacklePage = childFragmentManager.findFragmentByTag("f" + 3)
        if (tacklePage == null) {
            catchRecord.line = ""
            catchRecord.reel = ""
            catchRecord.rod = ""
        } else {
            catchRecord.line = tacklePage.lineField.text.toString()
            catchRecord.reel = tacklePage.reelField.text.toString()
            catchRecord.rod = tacklePage.rodField.text.toString()
        }

        // gps page
        catchRecord.location = "Here"
        catchRecord.date = formatter.format(date)

        return catchRecord
    }

}