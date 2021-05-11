package org.eidos.reader.ui.browse.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.eidos.reader.R
import org.eidos.reader.remote.requests.*
import org.eidos.reader.repository.EidosRepository
import java.lang.Exception

class BrowseSearchViewModel : ViewModel() {
    // TODO: use Flows

    private var _autocompleteResults = MutableLiveData<List<String>>()
    val autocompleteResults : LiveData<List<String>>
        get() = _autocompleteResults

    fun fetchAutocompleteResults(searchInput: String, searchTypeID: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _autocompleteResults.postValue(getAutocompleteResults(searchInput, searchTypeID))
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