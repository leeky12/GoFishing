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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ryalls.team.gofishing.R
import com.ryalls.team.gofishing.interfaces.ILaunchDetailView
import com.ryalls.team.gofishing.persistance.CatchRecord
import com.ryalls.team.gofishing.ui.catch_entry.CatchDetailsViewModel

/**
 * Demonstrates the use of [RecyclerView] with a [LinearLayoutManager]
 */
class CatchView : Fragment(), ILaunchDetailView {

    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var dataset: List<CatchRecord>

    private lateinit var catchViewModel: CatchDetailsViewModel // by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        layoutManager = LinearLayoutManager(activity)

        val adapter = CatchViewAdapter(this)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager

        // Get a new or existing ViewModel from the ViewModelProvider.
        val viewModel = ViewModelProvider(this).get(CatchDetailsViewModel::class.java)

        // Add an observer on the LiveData returned by getAlphabetizedWords.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        viewModel.allWords.observe(viewLifecycleOwner, Observer { words ->
            // Update the cached copy of the words in the adapter.
            words?.let { adapter.setWords(it) }
        })

        return rootView
    }

    companion object {
        private const val TAG = "RecyclerViewFragment"
        private const val KEY_LAYOUT_MANAGER = "layoutManager"
        private const val DATASET_COUNT = 60
    }

    override fun launchDetailView(dbID: Int) {
        val navController = findNavController()
        val bundle = bundleOf(
            Pair("dbID", "" + dbID)
        )
        navController.navigate(R.id.nav_details, bundle)
    }
}
