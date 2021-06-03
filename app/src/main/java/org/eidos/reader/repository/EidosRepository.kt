package org.eidos.reader.repository

import org.eidos.reader.model.Comment
import org.eidos.reader.model.Work
import org.eidos.reader.model.WorkBlurb
import org.eidos.reader.remote.AO3
import org.eidos.reader.remote.requests.AutocompleteRequest
import org.eidos.reader.remote.requests.CommentsRequest
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
class EidosRepository(private val remoteDataSource: AO3) {
    // TODO: Implement local data source and cache (not as important)

    fun getWorkFromAO3(workRequest: WorkRequest): Work {
        return remoteDataSource.getWork(workRequest)
    }

    fun getWorkBlurbsFromAO3(workFilterRequest: WorkFilterRequest): List<WorkBlurb> {
        return remoteDataSource.getWorkBlurbs(workFilterRequest)
    }

    fun getAutocompleteResultsFromAO3(autocompleteRequest: AutocompleteRequest): List<String> {
        return remoteDataSource.getAutocompleteResults(autocompleteRequest)
    }

    fun getCommentsFromAO3(commentsRequest: CommentsRequest): List<Comment> {
        return remoteDataSource.getComments(commentsRequest)
    }

    fun getWorkFromDatabase(workURL: String): Work {
        TODO("Not implemented yet")
    }
}