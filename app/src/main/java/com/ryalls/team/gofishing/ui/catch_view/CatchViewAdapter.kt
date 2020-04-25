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

package com.ryalls.team.gofishing.ui.catch_view


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.ryalls.team.gofishing.R
import com.ryalls.team.gofishing.`interface`.ILaunchDetailView

/**
 * Provide views to RecyclerView with data from dataSet.
 *
 * Initialize the dataset of the Adapter.
 *
 * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
 */
class CatchViewAdapter(private val dataSet: Array<String>, val catchView : ILaunchDetailView) :
    RecyclerView.Adapter<CatchViewAdapter.ViewHolder>() {

    val _catchView = catchView

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val location: TextView
        val date: TextView
        val species: TextView

        init {
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener {
                _catchView.launchDetailView(1)
                Log.d(TAG, "Element $adapterPosition clicked.")
           }
            location = v.findViewById(R.id.location)
            date = v.findViewById(R.id.date)
            species = v.findViewById(R.id.species)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view.
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.fish_item, viewGroup, false)

        return ViewHolder(v)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        Log.d(TAG, "Element $position set.")

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        viewHolder.location.text = dataSet[position]
        viewHolder.date.text = dataSet[position]
        viewHolder.species.text = dataSet[position]
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

    companion object {
        private const val TAG = "CustomAdapter"
    }
}
