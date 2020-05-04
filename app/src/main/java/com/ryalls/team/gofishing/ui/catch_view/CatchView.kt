/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.ryalls.team.gofishing.ui.catch_view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_SETTLING
import com.ryalls.team.gofishing.R
import com.ryalls.team.gofishing.interfaces.ILaunchAdapterInterface
import com.ryalls.team.gofishing.ui.catch_entry.CatchDetailsViewModel
import kotlinx.android.synthetic.main.app_bar_start_activity.*

/**
 * Demonstrates the use of [RecyclerView] with a [LinearLayoutManager]
 */
class CatchView : Fragment(), ILaunchAdapterInterface {

    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager

    private val viewModel: CatchDetailsViewModel by activityViewModels()


    override fun onResume() {
        super.onResume()
        activity?.fab?.show()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(
            R.layout.catch_fish_list,
            container, false
        ).apply { tag = TAG }

        recyclerView = rootView.findViewById(R.id.recyclerView)
        layoutManager = LinearLayoutManager(activity)

        val adapter = CatchViewAdapter(this)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager

        // Hide the fab button when scrolling and show when it's not scrolling
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && activity?.fab?.visibility == View.VISIBLE) {
                    activity?.fab?.hide()
                } else if (dy < 0 && activity?.fab?.visibility == View.VISIBLE) {
                    activity?.fab?.hide()
                }
            }
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == SCROLL_STATE_IDLE)
                {
                    activity?.fab?.show()
                }
            }

        })

        // Get a new or existing ViewModel from the ViewModelProvider.
        val viewModel = ViewModelProvider(this).get(CatchDetailsViewModel::class.java)

        // Add an observer on the LiveData returned by getAllCatchByCatchID.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        viewModel.allWords.observe(viewLifecycleOwner, Observer { catch ->
            // Update the cached copy of the words in the adapter.
            catch?.let { adapter.setWords(it) }
        })

        return rootView
    }

    companion object {
        private const val TAG = "RecyclerViewFragment"
    }

    override fun launchDetailView(dbID: Int) {
        val navController = findNavController()
        val bundle = bundleOf(
            Pair("dbID", "" + dbID)
        )
        navController.navigate(R.id.nav_details, bundle)
    }

    override fun launchDeleteDialog(dbID: Int) {
        val builder = AlertDialog.Builder(context as Context)
        builder.setTitle("Delete Catch")
        builder.setMessage("Do you wish to delete this catch?")
        builder.setPositiveButton(android.R.string.yes) { _, _ ->
            viewModel.deleteRecord(dbID)
        }
        builder.setNegativeButton(android.R.string.no) { _, _ ->
        }
        builder.show()
    }
}
