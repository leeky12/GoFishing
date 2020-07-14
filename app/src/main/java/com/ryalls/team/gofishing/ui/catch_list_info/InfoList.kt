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

package com.ryalls.team.gofishing.ui.catch_list_info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ryalls.team.gofishing.R
import com.ryalls.team.gofishing.ui.catch_entry.CatchDetailsViewModel
import com.ryalls.team.gofishing.utils.KeyboardUtils

/**
 * Demonstrates the use of [RecyclerView] with a [LinearLayoutManager]
 */
class InfoList : DialogFragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager

    private val viewModel: CatchDetailsViewModel by activityViewModels()


    override fun onResume() {
        super.onResume()
        KeyboardUtils().closeKeyboard(requireContext(), view)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(
            R.layout.info_list,
            container, false
        ).apply { tag = TAG }

        recyclerView = rootView.findViewById(R.id.infoListView)
        layoutManager = LinearLayoutManager(activity)

        val adapter = InfoListAdapter()

        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager

        viewModel.fishCountReady.observe(viewLifecycleOwner, Observer { catch ->
            adapter.setList(viewModel.speciesCount)
        })

        return rootView
    }

    companion object {
        private const val TAG = "RecyclerViewFragment"
    }

}
