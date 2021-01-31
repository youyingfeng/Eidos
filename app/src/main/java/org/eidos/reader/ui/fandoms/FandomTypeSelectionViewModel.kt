package org.eidos.reader.ui.fandoms

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.eidos.reader.remote.AO3
import org.eidos.reader.remote.requests.AutocompleteRequest
import org.eidos.reader.remote.requests.FandomAutocompleteRequest
import org.eidos.reader.repository.EidosRepository

class FandomTypeSelectionViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    private var _autocompleteResults = MutableLiveData<List<String>>()
    val autocompleteResults : LiveData<List<String>>
        get() = _autocompleteResults

    // flag for network error
    private var _eventNetworkError = MutableLiveData<Boolean>(false)
    val eventNetworkError : LiveData<Boolean>
        get() = _eventNetworkError

    fun fetchAutocompleteResults(searchInput: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _autocompleteResults.postValue(getAutocompleteResults(searchInput))
        }
    }

    private suspend fun getAutocompleteResults(searchInput: String) : List<String> {
        val autocompleteRequest = FandomAutocompleteRequest(searchInput)
        return EidosRepository.getAutocompleteResultsFromAO3(autocompleteRequest)
    }
}
