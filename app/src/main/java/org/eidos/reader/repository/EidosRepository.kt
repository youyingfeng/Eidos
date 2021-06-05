package org.eidos.reader.repository

import org.eidos.reader.model.Comment
import org.eidos.reader.model.Work
import org.eidos.reader.model.WorkBlurb
import org.eidos.reader.remote.AO3
import org.eidos.reader.remote.requests.AutocompleteRequest
import org.eidos.reader.remote.requests.CommentsRequest
import org.eidos.reader.remote.requests.WorkFilterRequest
import org.eidos.reader.remote.requests.WorkRequest
import org.eidos.reader.storage.Storage

/**
 * This repository/facade provides a unified API through which the ViewModel can interact with to
 * retrieve data.
 * All requests for data, be it local or remote, will be made through this repository and not
 * through the individual components (e.g. the AO3 class)
 *
 * This class is implemented as a singleton first (object keyword), but will have to be reworked to
 * adhere to proper dependency injection. Currently this class is not very testable as-is.
 */
class EidosRepository(private val remoteDataSource: AO3, private val localDataSource: Storage) {
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

    fun getWorkBlurbsFromDatabase(): List<WorkBlurb> {
        return localDataSource.getAllWorkBlurbs()
    }

    fun getWorkFromDatabase(workURL: String): Work {
        return localDataSource.getWork(workURL)
    }

    fun insertWorkIntoDatabase(work: Work) {
        return localDataSource.insertWork(work)
    }

    fun deleteWorkFromDatabase(workURL: String) {
        return localDataSource.deleteWork(workURL)
    }

    fun deleteAllWorksFromDatabase(workURL: String) {
        return localDataSource.deleteAllWorks()
    }

    private fun insertWorkIntoDatabase(workURL: String) {
        TODO("Do not implement until MVP complete")
        // unprivate this function once implemented
    }
}