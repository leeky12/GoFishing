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


import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ryalls.team.gofishing.R
import com.ryalls.team.gofishing.interfaces.ILaunchAdapterInterface
import com.ryalls.team.gofishing.persistance.CatchRecord


/**
 * Provide views to RecyclerView with data from dataSet.
 *
 * Initialize the dataset of the Adapter.
 *
 * @param catchView ILaunchAdapterInterface to launch the various adapters required
 */
class CatchViewAdapter(
    private val catchView: ILaunchAdapterInterface
) :
    RecyclerView.Adapter<CatchViewAdapter.ViewHolder>() {
    private var catchList = emptyList<CatchRecord>() // Cached copy of words


    internal fun setWords(catchRecords: List<CatchRecord>) {
        this.catchList = catchRecords
        notifyDataSetChanged()
    }

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v), View.OnLongClickListener,
        View.OnClickListener {
        val location: TextView
        val date: TextView
        val species: TextView
        var catchID: Int = 0
        var thumbnail: ImageView

        init {
            v.setOnClickListener(this)
            v.setOnLongClickListener(this)
            location = v.findViewById(R.id.location)
            date = v.findViewById(R.id.date)
            species = v.findViewById(R.id.species)
            thumbnail = v.findViewById(R.id.fishThumbnail)
        }

        override fun onLongClick(v: View?): Boolean {
            catchView.launchDeleteDialog(catchID)
            return true
        }

        override fun onClick(v: View?) {
            catchView.launchDetailView(catchID)
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
    override fun onBindViewHolder(viewHolder: ViewHolder, positionInList: Int) {
        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        viewHolder.location.text = catchList[positionInList].location
        viewHolder.date.text = catchList[positionInList].date
        viewHolder.species.text = catchList[positionInList].species
        viewHolder.catchID = catchList[positionInList].catchID
        if (catchList[positionInList].thumbnail.isNotEmpty()) {
            val toByteArray = Base64.decode(catchList[positionInList].thumbnail, Base64.DEFAULT)
            viewHolder.thumbnail.setImageBitmap(
                BitmapFactory.decodeByteArray(
                    toByteArray,
                    0,
                    toByteArray.size
                )
            )
        } else {
            // ensure that previous views are overridden so if there is no image you get the default resource
            viewHolder.thumbnail.setImageResource(R.drawable.ic_fisher)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = catchList.size

    companion object {
        private const val TAG = "CustomAdapter"
    }
}
