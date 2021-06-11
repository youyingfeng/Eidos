package org.eidos.reader.ui.browse.worklist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.eidos.reader.model.Comment

import org.eidos.reader.model.WorkBlurb
import org.eidos.reader.remote.choices.WorkFilterChoices
import org.eidos.reader.remote.requests.WorkFilterRequest
import org.eidos.reader.remote.requests.WorkRequest
import org.eidos.reader.repository.EidosRepository
import timber.log.Timber

class WorkListViewModel
    constructor(
        val workFilterRequest: WorkFilterRequest,
        val repository: EidosRepository
    )
    : ViewModel()
{

    /* BEGIN Variables for WorkListFragment */
    private var _workBlurbs = MutableLiveData<List<WorkBlurb>>(emptyList())
    val workBlurbs: LiveData<List<WorkBlurb>>
        get() = _workBlurbs

    private var largestPageNumber = 1

    // possibly hacky fix, see if it is possible to improve.
    private var isFetchingWorks = false
    /* END Variables for WorkListFragment */

    /* BEGIN Variables for FilterDialogFragment */
    // TODO: add arrays to store shit
    // Note: autocomplete stuff should be shoved in another ViewModel
    /* END Variables for FilterDialogFragment */

    init {
        initialiseWorkBlurbs()
    }

    private fun initialiseWorkBlurbs() {
        // clear the data
        _workBlurbs.value = emptyList()

        Timber.i("Fetching Work Blurbs")
        viewModelScope.launch(Dispatchers.IO) {
            _workBlurbs.postValue(getWorkBlurbs(workFilterRequest))
            Timber.i("WorkBlurbs successfully fetched")
        }
    }

    private suspend fun getWorkBlurbs(request: WorkFilterRequest) : List<WorkBlurb> {
        return repository.getWorkBlurbsFromAO3(request)
    }

    fun getNextPage() {
        if (!isFetchingWorks) {
            // set the bool to stop repeated fetching
            isFetchingWorks = true
            largestPageNumber++
            Timber.i("getNextPage() called")

            workFilterRequest.pageNumber = largestPageNumber
//            workFilterRequest.updateQueryString()

            val currentList : MutableList<WorkBlurb> = workBlurbs.value!!.toMutableList()

            viewModelScope.launch(Dispatchers.IO) {
                currentList.addAll(getWorkBlurbs(workFilterRequest))
                _workBlurbs.postValue(currentList)
                isFetchingWorks = false
                Timber.i("More WorkBlurbs successfully fetched")
            }
        } else {
            Timber.i("Currently fetching works, extra requests blocked")
        }
    }

    fun resetPages() {
        _workBlurbs.value = emptyList()
        largestPageNumber = 1
    }

    fun updateFilterChoices(choices: WorkFilterChoices) {
        workFilterRequest.updateChoices(choices)
        resetPages()
        initialiseWorkBlurbs()
    }

    fun addWorkToLibrary(workBlurb: WorkBlurb) {
        val work = repository.getWorkFromAO3(WorkRequest(workBlurb.workURL))
        repository.insertWorkIntoDatabase(work)
    }

    fun addWorkToReadingList(workBlurb: WorkBlurb) {
        repository.addWorkBlurbToReadingList(workBlurb)
    }
}
