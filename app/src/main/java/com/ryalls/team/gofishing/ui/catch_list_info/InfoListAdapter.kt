/*
* Copyright (C) 2014 The Android Open Source Project
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


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ryalls.team.gofishing.R
import com.ryalls.team.gofishing.data.SpeciesCount


/**
 * Provide views to RecyclerView with data from dataSet.
 *
 * Initialize the dataset of the Adapter.
 *
 * @param catchView ILaunchAdapterInterface to launch the various adapters required
 */
class InfoListAdapter :

    RecyclerView.Adapter<InfoListAdapter.ViewHolder>() {
    private var catchList = emptyList<SpeciesCount>() // Cached copy of words

    internal fun setList(catchRecords: List<SpeciesCount>) {
        this.catchList = catchRecords
        for (species in catchList) {
            Log.d("InInfo", species.species)
        }
        notifyDataSetChanged()
    }

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val speciesname: TextView = v.findViewById(R.id.species_name)
        val speciescount: TextView = v.findViewById(R.id.species_count)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view.
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.catch_count, viewGroup, false)

        return ViewHolder(v)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, positionInList: Int) {
        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        viewHolder.speciesname.text = catchList[positionInList].species
        viewHolder.speciescount.text = catchList[positionInList].count.toString()
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = catchList.size

}
