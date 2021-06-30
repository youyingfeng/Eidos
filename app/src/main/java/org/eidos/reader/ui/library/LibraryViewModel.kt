package org.eidos.reader.ui.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.eidos.reader.model.domain.WorkBlurb
import org.eidos.reader.repository.EidosRepository
import timber.log.Timber

class LibraryViewModel(val repository: EidosRepository) : ViewModel() {
    // TODO: Implement the ViewModel

    // TODO: write code to retrieve works from DB

    private var _workBlurbs = MutableLiveData<List<WorkBlurb>>(emptyList())
    val workBlurbs: LiveData<List<WorkBlurb>>
        get() = _workBlurbs

//    init {
//        fetchWorkBlurbsFromDatabase()
//        Timber.i("init complete")
//    }

    fun fetchWorkBlurbsFromDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            _workBlurbs.postValue(repository.getWorkBlurbsFromDatabase())
            Timber.i("WorkBlurbs successfully fetched from DB")
        }
    }

    fun deleteWorkFromLibrary(workBlurb: WorkBlurb) {
        val updatedWorkBlurbs = workBlurbs.value!!.toMutableList()
        updatedWorkBlurbs.remove(workBlurb)
        _workBlurbs.value = updatedWorkBlurbs
        // TODO: update to WorkManager
        CoroutineScope(Dispatchers.IO).launch {
            repository.deleteWorkFromDatabase(workBlurb.workURL)
        }

    }

    fun addWorkToReadingList(workBlurb: WorkBlurb) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.addWorkBlurbToReadingList(workBlurb)
        }
    }
}