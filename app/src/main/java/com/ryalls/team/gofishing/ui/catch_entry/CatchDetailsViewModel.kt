package com.ryalls.team.gofishing.ui.catch_entry

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ryalls.team.gofishing.persistance.CatchRecord
import com.ryalls.team.gofishing.persistance.CatchRepository
import com.ryalls.team.gofishing.persistance.CatchRoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*


class CatchDetailsViewModel(application: Application) : AndroidViewModel(application) {

    var repository: CatchRepository

    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    var allWords: LiveData<List<CatchRecord>>

    var catchRecord : CatchRecord = CatchRecord("")

    // Mutable String used to indicate the list has been completely filled  with the data
    val recordReady: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }


    init {
        val catchDao = CatchRoomDatabase.getDatabase(context = application, scope = viewModelScope).catchDao()
        repository = CatchRepository(catchDao)
        allWords = repository.allWords
     }

    fun getRecord(recordID : Int)
    {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                Log.d("TestCoroutine", "Started " + recordID)
                catchRecord = repository.getRecord(recordID)
                Log.d("TestCoroutine", "Finished")
            }
            withContext(Dispatchers.Main) {
                Log.d("TestCoroutine", "" + (catchRecord?.weight ?: "Fail"))
                recordReady.value = "True"
            }
        }
    }

    fun updatesDetailsCatch(lure :  String, structure:  String, conditions:  String, depth:  String,
    hook : String, groundbait :  String, boatspeed:  String, tides:  String)
    {
//        catchRecord.lure = lure
//        catchRecord.structure = structure
//        catchRecord.conditions = conditions
//        catchRecord.depth = depth
//        catchRecord.hook = hook
//        catchRecord.groundBait = groundbait
//        catchRecord.boatspeed = boatspeed
//        catchRecord.tides = tides
    }

    fun updatesBasicCatch(species :  String, comment:  String, weight:  String, length:  String)
    {
//        catchRecord.species = species
//        catchRecord.comments = comment
//        catchRecord.weight = weight
//        catchRecord.length = length
    }

    fun update(updateRecord : CatchRecord) = viewModelScope.launch(Dispatchers.IO) {
        updateRecord.catchID = catchRecord.catchID
        Log.d("CatchRecord", "" + updateRecord.catchID)
        repository.update(updateRecord)
    }

    fun deleteRecord(catchID : Int) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteRecord(catchID)
    }
    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(catchRecord : CatchRecord) = viewModelScope.launch(Dispatchers.IO) {
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        val date = Date()
        catchRecord.date = formatter.format(date)

        Log.d("CatchRecord", catchRecord.species + " " + catchRecord.date + " " + catchRecord.location)
        repository.insert(catchRecord)
    }


}