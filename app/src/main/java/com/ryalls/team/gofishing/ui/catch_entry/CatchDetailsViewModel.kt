package com.ryalls.team.gofishing.ui.catch_entry

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.ryalls.team.gofishing.persistance.CatchRecord
import com.ryalls.team.gofishing.persistance.CatchRepository
import com.ryalls.team.gofishing.persistance.CatchRoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class CatchDetailsViewModel(application: Application) : AndroidViewModel(application) {

    var repository: CatchRepository

    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    var allWords: LiveData<List<CatchRecord>>

    init {
        val catchDao = CatchRoomDatabase.getDatabase(context = application, scope = viewModelScope).catchDao()
        repository = CatchRepository(catchDao)
        allWords = repository.allWords
    }


    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(catchRecord: CatchRecord) = viewModelScope.launch(Dispatchers.IO) {
        catchRecord.hook = "Large"
        catchRecord.boatspeed = "fast"
        catchRecord.conditions = "Good"
        catchRecord.depth = "deep"
        catchRecord.groundBait = "fluffy"
        catchRecord.length = "12"
        catchRecord.line = "4lb"
        catchRecord.lure = "wiggly one"
        catchRecord.mComments = "Really hard fight"
        catchRecord.reel = "Okuma"
        catchRecord.rod = "carbonactive"
        catchRecord.structure = "woody"
        catchRecord.tides = "high"
        catchRecord.weight = "15lb"

        repository.insert(catchRecord)
    }


}