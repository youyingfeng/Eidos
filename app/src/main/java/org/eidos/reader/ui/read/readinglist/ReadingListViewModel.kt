package org.eidos.reader.ui.read.readinglist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.eidos.reader.model.domain.WorkBlurb
import org.eidos.reader.remote.requests.WorkRequest
import org.eidos.reader.repository.EidosRepository
import timber.log.Timber

class ReadingListViewModel(val repository: EidosRepository) : ViewModel() {
    // TODO: Implement the ViewModel

    private var _workBlurbs = MutableLiveData<List<WorkBlurb>>(emptyList())
    val workBlurbs: LiveData<List<WorkBlurb>>
        get() = _workBlurbs

//    init {
//        fetchWorkBlurbsFromDatabase()
//        Timber.i("init complete")
//    }

    fun fetchWorkBlurbsFromDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            _workBlurbs.postValue(repository.getWorkBlurbsFromReadingList())
            Timber.i("WorkBlurbs successfully fetched from DB")
        }
    }

    fun addWorkToLibrary(workBlurb: WorkBlurb) {
        // use globalscope/coroutinescope first as we want this work to continue
        // when there is time, update to workmanager
        CoroutineScope(Dispatchers.IO).launch {
            val work = repository.getWorkFromAO3(WorkRequest(workBlurb.workURL))
            repository.insertWorkIntoDatabase(work)
        }
    }

    fun removeWorkFromReadingList(workBlurb: WorkBlurb) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.removeWorkFromReadingList(workBlurb.workURL)
        }
    }
}