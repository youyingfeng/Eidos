package org.eidos.reader.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.eidos.reader.model.Work
import org.eidos.reader.model.WorkBlurb
import org.eidos.reader.remote.AO3
import org.eidos.reader.remote.requests.AutocompleteRequest
import org.eidos.reader.remote.requests.WorkFilterRequest
import org.eidos.reader.remote.requests.WorkRequest

/**
 * This repository/facade provides a unified API through which the ViewModel can interact with to
 * retrieve data.
 * All requests for data, be it local or remote, will be made through this repository and not
 * through the individual components (e.g. the AO3 class)
 *
 * This class is implemented as a singleton first (object keyword), but will have to be reworked to
 * adhere to proper dependency injection. Currently this class is not very testable as-is.
 */
object EidosRepository {
    private val remote: AO3 = AO3()

    fun getWorkFromAO3(workRequest: WorkRequest): Work {
        return remote.getWork(workRequest)
    }

    fun getWorkBlurbsFromAO3(workFilterRequest: WorkFilterRequest): List<WorkBlurb> {
        return remote.getWorkBlurbs(workFilterRequest)
    }

    fun getAutocompleteResultsFromAO3(autocompleteRequest: AutocompleteRequest): List<String> {
        return remote.getAutocompleteResults(autocompleteRequest)
    }

    // TODO: Change the repo and VM to use flows
    val _autocompleteResults : Flow<List<String>> = flowOf(listOf<String>())

    suspend fun updateAutocompleteResults(autocompleteRequest: AutocompleteRequest) {
        // update the value of flow
    }
}