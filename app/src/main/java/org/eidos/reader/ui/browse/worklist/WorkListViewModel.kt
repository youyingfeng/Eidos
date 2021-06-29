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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

import org.eidos.reader.model.WorkBlurb
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

//    private lateinit var workFilterRequest: WorkFilterRequest

    private val _workBlurbs = MutableLiveData<List<WorkBlurb>>(emptyList())
    val workBlurbs: LiveData<List<WorkBlurb>>
        get() = _workBlurbs

    private var _workBlurbFlow = MutableLiveData<Flow<PagingData<WorkBlurb>>>()
    val workBlurbFlow: LiveData<Flow<PagingData<WorkBlurb>>> get() = _workBlurbFlow

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
                
//                searchWorkBlurbs()

//                resetPages()
//                getNextPage()
            } catch (e: Exception) {
                _exception.postValue(e)
            }

        }
    }

    fun searchWorkBlurbs(): Flow<PagingData<WorkBlurb>> {
        Timber.i("wtf")
        val newResult = repository.getWorkBlurbStreamFromAO3(workFilterRequest)
            .cachedIn(viewModelScope)
        Timber.i("loaded")
//        _workBlurbFlow.postValue(newResult)
        Timber.i("loaded")
        return newResult
    }


    fun updateFilterChoices(choices: WorkFilterChoices): Boolean {
        if (workFilterRequest.workFilterChoices != choices) {
            workFilterRequest.updateChoices(choices)
            return true
        } else {
            return false
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
