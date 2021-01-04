package org.eidos.reader.ui.worklist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import org.eidos.reader.model.WorkBlurb
import org.eidos.reader.remote.AO3
import org.eidos.reader.remote.requests.WorkFilterRequest
import timber.log.Timber

class WorkListViewModel(private val workFilterRequest: WorkFilterRequest) : ViewModel() {
    // TODO: make the backend use flows
    // worklist can be left as a mutablelivedata since it originates in the VM.
    private var _workBlurbs = MutableLiveData<List<WorkBlurb>>()
    val workBlurbs: LiveData<List<WorkBlurb>>
        get() = _workBlurbs

    init {
        initialiseWorkBlurbs()
    }

    private fun initialiseWorkBlurbs() {
        Timber.i("Fetching Work Blurbs")
        viewModelScope.launch(Dispatchers.IO) {
            _workBlurbs.postValue(getWorkBlurbs())
            Timber.i("WorkBlurbs successfully fetched")
        }
    }

    private suspend fun getWorkBlurbs() : List<WorkBlurb> {
        return AO3.getWorkBlurbs(workFilterRequest)
    }
}