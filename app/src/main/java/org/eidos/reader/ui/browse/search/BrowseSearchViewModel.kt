package org.eidos.reader.ui.browse.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import org.eidos.reader.R
import org.eidos.reader.remote.requests.*
import org.eidos.reader.repository.EidosRepository
import timber.log.Timber
import java.lang.Exception

class BrowseSearchViewModel(val repository: EidosRepository) : ViewModel() {
    private var _autocompleteResults = MutableLiveData<List<String>>()
    val autocompleteResults : LiveData<List<String>>
        get() = _autocompleteResults

    private var currentRunningJob: Job? = null

    fun fetchAutocompleteResults(searchInput: String, searchTypeID: Int) {
        currentRunningJob?.cancel()
        currentRunningJob = viewModelScope.launch(Dispatchers.IO) {
            delay(100L) // this is just to prevent API spam, although im not sure how much this helps
            // the isActive check enables cancellation, as coroutine cancellation is cooperative
            val result = if (isActive) getAutocompleteResults(searchInput, searchTypeID) else return@launch
            if (isActive) _autocompleteResults.postValue(result)
        }
    }

    private suspend fun getAutocompleteResults(searchInput: String, searchTypeID: Int) : List<String> {
        val autocompleteRequest = when (searchTypeID) {
            R.id.allTagsChip -> TagAutocompleteRequest(searchInput)
            R.id.fandomsChip -> FandomAutocompleteRequest(searchInput)
            R.id.charactersChip -> CharacterAutocompleteRequest(searchInput)
            R.id.relationshipsChip -> RelationshipAutocompleteRequest(searchInput)
            R.id.freeformsChip -> FreeformAutocompleteRequest(searchInput)
            R.id.usersChip -> PseudAutocompleteRequest(searchInput)
            else -> throw Exception("what dis yuhh this be wrong ID man")
        }
        return repository.getAutocompleteResultsFromAO3(autocompleteRequest)
    }
}