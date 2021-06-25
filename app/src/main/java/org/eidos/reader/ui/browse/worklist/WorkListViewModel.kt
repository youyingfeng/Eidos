package org.eidos.reader.ui.browse.worklist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import org.eidos.reader.model.WorkBlurb
import org.eidos.reader.network.Network
import org.eidos.reader.remote.AO3
import org.eidos.reader.remote.choices.WorkFilterChoices
import org.eidos.reader.remote.requests.WorkFilterRequest
import org.eidos.reader.repository.EidosRepository
import org.eidos.reader.ui.misc.utilities.SingleLiveEvent
import org.eidos.reader.workers.DownloadWorker
import timber.log.Timber

class WorkListViewModel
    constructor(
        private val repository: EidosRepository,
        private var workFilterRequest: WorkFilterRequest
    )
    : ViewModel()
{

//    private lateinit var workFilterRequest: WorkFilterRequest

    private val _workBlurbs = MutableLiveData<List<WorkBlurb>>(emptyList())
    val workBlurbs: LiveData<List<WorkBlurb>>
        get() = _workBlurbs

    private val _tagName = MutableLiveData<String>("")
    val tagName: LiveData<String>
        get() = _tagName

    private val _workCount = MutableLiveData<Int>(0)
    val workCount: LiveData<Int>
        get() = _workCount

    private val _exception = SingleLiveEvent<Exception>()
    val exception: LiveData<Exception>
        get() = _exception


    private var largestPageNumber = 0
    private var isFetchingWorks = false

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val workSearchMetadata = try {
                    repository.getWorkSearchMetadataFromAO3(workFilterRequest)
                } catch (e: AO3.TagSynonymException) {
                    Timber.i(e.redirectedTagName)
                    workFilterRequest = workFilterRequest.copy(tagName = e.redirectedTagName)
                    repository.getWorkSearchMetadataFromAO3(workFilterRequest)
                }

                _tagName.postValue(workSearchMetadata.tagName)
                _workCount.postValue(workSearchMetadata.workCount)

                resetPages()
                getNextPage()
            } catch (e: Exception) {
                _exception.postValue(e)
            }

        }
    }

    fun initialiseWithRequest(workFilterRequest: WorkFilterRequest) {

    }

    fun updateFilterChoices(choices: WorkFilterChoices) {
        workFilterRequest.updateChoices(choices)
        _workBlurbs.value = emptyList()
        largestPageNumber = 0   // this is to avoid a race condition when calling this in main thread
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

    private suspend fun resetPages() {
        _workBlurbs.postValue(emptyList())
        largestPageNumber = 0
    }

    fun addWorkToLibrary(workBlurb: WorkBlurb) {
        repository.insertWorkIntoDatabase(workBlurb.workURL)
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
