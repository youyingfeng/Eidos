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
import java.lang.Exception

class BrowseSearchViewModel : ViewModel() {
    // TODO: use Flows

    private var _autocompleteResults = MutableLiveData<List<String>>()
    val autocompleteResults : LiveData<List<String>>
        get() = _autocompleteResults

    private lateinit var currentRunningJob: Job

    fun fetchAutocompleteResults(searchInput: String, searchTypeID: Int) {
        // FIXME: This is a hacky ass fix - nested coroutines? wtf bro.
        // FIXME: but it works.
        viewModelScope.launch {
            if (::currentRunningJob.isInitialized) {
                currentRunningJob.cancelAndJoin()
            }

            currentRunningJob = viewModelScope.launch(Dispatchers.IO) {
                delay(100L)
                val result = getAutocompleteResults(searchInput, searchTypeID)
                _autocompleteResults.postValue(result)
            }
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
        return EidosRepository.getAutocompleteResultsFromAO3(autocompleteRequest)
    }
}