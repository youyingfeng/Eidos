package org.eidos.reader.ui.browse.worklist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

import org.eidos.reader.model.WorkBlurb
import org.eidos.reader.model.WorkSearchMetadata
import org.eidos.reader.remote.AO3
import org.eidos.reader.remote.choices.WorkFilterChoices
import org.eidos.reader.remote.requests.WorkFilterRequest
import org.eidos.reader.repository.EidosRepository
import org.eidos.reader.ui.misc.utilities.SingleLiveEvent
import timber.log.Timber

class WorkListViewModel
    constructor(
        private val repository: EidosRepository,
        private var workFilterRequest: WorkFilterRequest
    )
    : ViewModel()
{
    init {
        fetchMetadataAndWorkBlurbStream()
    }

    private var _workBlurbFlow = MutableLiveData<Flow<PagingData<WorkBlurb>>>()
    val workBlurbFlow: LiveData<Flow<PagingData<WorkBlurb>>> get() = _workBlurbFlow

    private val _metadata = MutableLiveData<WorkSearchMetadata>()
    val metadata: LiveData<WorkSearchMetadata> get() = _metadata

    private val _exception = SingleLiveEvent<Exception>()
    val exception: LiveData<Exception>
        get() = _exception

    private fun fetchMetadataAndWorkBlurbStream() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val workSearchMetadata = try {
                    repository.getWorkSearchMetadataFromAO3(workFilterRequest)
                } catch (e: AO3.TagSynonymException) {
                    Timber.i(e.redirectedTagName)
                    workFilterRequest = workFilterRequest.copy(tagName = e.redirectedTagName)
                    repository.getWorkSearchMetadataFromAO3(workFilterRequest)
                }
                _metadata.postValue(workSearchMetadata)

                searchWorkBlurbs()  // must be called inside here to avoid race condition
            } catch (e: Exception) {
                _exception.postValue(e)
            }
        }
    }

    private fun searchWorkBlurbs() {
        val newResult = repository.getWorkBlurbStreamFromAO3(workFilterRequest)
            .cachedIn(viewModelScope)
        _workBlurbFlow.postValue(newResult)
    }

    fun updateFilterChoices(choices: WorkFilterChoices) {
        if (workFilterRequest.workFilterChoices != choices) {
            workFilterRequest.updateChoices(choices)
            fetchMetadataAndWorkBlurbStream()
        }
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
