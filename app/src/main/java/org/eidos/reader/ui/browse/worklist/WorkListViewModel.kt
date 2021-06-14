package org.eidos.reader.ui.browse.worklist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.eidos.reader.EidosApplication
import org.eidos.reader.model.Comment

import org.eidos.reader.model.WorkBlurb
import org.eidos.reader.remote.choices.WorkFilterChoices
import org.eidos.reader.remote.requests.WorkFilterRequest
import org.eidos.reader.remote.requests.WorkRequest
import org.eidos.reader.repository.EidosRepository
import org.eidos.reader.workers.DownloadWorker
import timber.log.Timber

class WorkListViewModel
    constructor(
        private val repository: EidosRepository,
        private val workManager: WorkManager
    )
    : ViewModel()
{

    private var _workBlurbs = MutableLiveData<List<WorkBlurb>>(emptyList())
    val workBlurbs: LiveData<List<WorkBlurb>>
        get() = _workBlurbs

    private lateinit var workFilterRequest: WorkFilterRequest

    private var largestPageNumber = 0
    private var isFetchingWorks = false

    fun initialiseWithRequest(workFilterRequest: WorkFilterRequest) {
        this.workFilterRequest = workFilterRequest
        resetPages()
        getNextPage()
    }

    fun updateFilterChoices(choices: WorkFilterChoices) {
        workFilterRequest.updateChoices(choices)
        resetPages()
        getNextPage()
    }

    fun getNextPage() {
        if (!isFetchingWorks) {
            Timber.i("getNextPage() called")
            // set the bool to stop repeated fetching
            isFetchingWorks = true
            largestPageNumber++
            workFilterRequest.pageNumber = largestPageNumber    // guaranteed to be >= 1
            val currentList : MutableList<WorkBlurb> = workBlurbs.value!!.toMutableList()

            viewModelScope.launch(Dispatchers.IO) {
                currentList.addAll(repository.getWorkBlurbsFromAO3(workFilterRequest))
                _workBlurbs.postValue(currentList)
                isFetchingWorks = false
                Timber.i("More WorkBlurbs successfully fetched")
            }
        } else {
            Timber.i("Currently fetching works, extra requests blocked")
        }
    }

    private fun resetPages() {
        _workBlurbs.value = emptyList()
        largestPageNumber = 0
    }

    fun addWorkToLibrary(workBlurb: WorkBlurb) {
        workManager.enqueue(DownloadWorker.createDownloadRequest(workBlurb.workURL))
    }

    fun addWorkToReadingList(workBlurb: WorkBlurb) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.addWorkBlurbToReadingList(workBlurb)
        }
    }

    fun getWorkFilterChoices(): WorkFilterChoices {
        return workFilterRequest.workFilterChoices.copy()
    }
}
