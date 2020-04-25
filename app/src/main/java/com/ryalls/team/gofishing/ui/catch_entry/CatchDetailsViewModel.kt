package com.ryalls.team.gofishing.ui.catch_entry

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ryalls.team.gofishing.data.CatchData

class CatchDetailsViewModel : ViewModel() {

    val catchReady: MutableLiveData<CatchData> by lazy {
        MutableLiveData<CatchData>()
    }

    fun setCatchDetails(dbID: String) {
        var catch = CatchData(dbID)
        catchReady.value = catch
    }

}