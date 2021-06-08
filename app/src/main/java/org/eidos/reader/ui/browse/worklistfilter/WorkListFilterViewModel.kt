package org.eidos.reader.ui.browse.worklistfilter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.eidos.reader.remote.choices.WorkFilterChoices
import org.eidos.reader.remote.requests.TagAutocompleteRequest
import org.eidos.reader.repository.EidosRepository

class WorkListFilterViewModel
    constructor(
        private val repository: EidosRepository,
        var workFilterChoices: WorkFilterChoices
    )
    : ViewModel()
{
    /**
     * We can use one LiveData field to supply results for multiple input fields, because only one
     * input field can be active at any given time.
     */
    private var _autocompleteResults = MutableLiveData<List<String>>(emptyList())
    val autocompleteResults : LiveData<List<String>>
        get() = _autocompleteResults

    fun fetchAutocompleteResults(searchInput: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val autocompleteRequest = TagAutocompleteRequest(searchInput)
            val results = repository.getAutocompleteResultsFromAO3(autocompleteRequest)
            _autocompleteResults.postValue(results)
        }
    }

    fun clearAutocompleteResults() {
        _autocompleteResults.value = emptyList()
    }
}
