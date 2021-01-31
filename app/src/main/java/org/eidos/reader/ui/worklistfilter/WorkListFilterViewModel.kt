package org.eidos.reader.ui.worklistfilter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.eidos.reader.remote.requests.FandomAutocompleteRequest
import org.eidos.reader.repository.EidosRepository

class WorkListFilterViewModel : ViewModel() {
    /**
     * We can use one LiveData field to supply results for multiple input fields, because only one
     * input field can be active at any given time.
     */
    private var _autocompleteResults = MutableLiveData<List<String>>(emptyList())
    val autocompleteResults : LiveData<List<String>>
        get() = _autocompleteResults

    /* Store the state of the form */
    val includedTags: List<String> = emptyList()
    val excludedTags: List<String> = emptyList()

    val checkboxChoicesMap = mapOf(
            // ratings
            "ratingGen" to false,
            "ratingTeen" to false,
            "ratingMature" to false,
            "ratingExplicit" to false,
            "ratingNone" to false,
            // warnings
            "warningNone" to false,
            "warningViolence" to false,
            "warningCharacterDeath" to false,
            "warningUnderage" to false,
            "warningRape" to false,
            "warningChoseNoWarnings" to false,
            // categories
            "categoryGen" to false,
            "categoryFM" to false,
            "categoryMM" to false,
            "categoryFF" to false,
            "categoryMulti" to false,
            "categoryOther" to false,
            // crossovers
            "showCrossovers" to true,
            "showNonCrossovers" to true,
            // completion status
            "showCompletedWorks" to true,
            "showIncompleteWorks" to true
    )

    val rangesMap = mapOf(
            "wordCount" to Pair("", ""),
            "dateUpdated" to Pair("", "")
    )

    fun fetchAutocompleteResults(searchInput: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val results = getAutocompleteResults(searchInput)
            _autocompleteResults.postValue(results)
        }
    }

    fun clearAutocompleteResults() {
        _autocompleteResults.value = emptyList()
    }

    private suspend fun getAutocompleteResults(searchInput: String) : List<String> {
        val autocompleteRequest = FandomAutocompleteRequest(searchInput)
        return EidosRepository.getAutocompleteResultsFromAO3(autocompleteRequest)
    }
}
