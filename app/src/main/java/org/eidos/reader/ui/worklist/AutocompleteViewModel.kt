package org.eidos.reader.ui.worklist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.eidos.reader.remote.requests.FandomAutocompleteRequest
import org.eidos.reader.repository.EidosRepository

class AutocompleteViewModel : ViewModel() {

    private var _autocompleteResults = MutableLiveData<Array<String>>()
    val autocompleteResults : LiveData<Array<String>>
        get() = _autocompleteResults

    fun fetchAutocompleteResults(searchInput: String, callback: (Array<String>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val results = getAutocompleteResults(searchInput).toTypedArray()
            _autocompleteResults.postValue(results)
        }
    }

    fun clearAutocompleteResults() {
        _autocompleteResults.value = emptyArray()
    }

    private suspend fun getAutocompleteResults(searchInput: String) : List<String> {
        val autocompleteRequest = FandomAutocompleteRequest(searchInput)
        return EidosRepository.getAutocompleteResultsFromAO3(autocompleteRequest)
    }
}