package org.eidos.reader.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.work.WorkManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import org.eidos.reader.ReadingListWorkBlurb
import org.eidos.reader.SavedWorkBlurb
import org.eidos.reader.model.domain.Comment
import org.eidos.reader.model.domain.Work
import org.eidos.reader.model.domain.WorkBlurb
import org.eidos.reader.model.domain.WorkSearchMetadata
import org.eidos.reader.model.ui.WorkBlurbUiModel
import org.eidos.reader.remote.AO3
import org.eidos.reader.remote.requests.AutocompleteRequest
import org.eidos.reader.remote.requests.CommentsRequest
import org.eidos.reader.remote.requests.WorkFilterRequest
import org.eidos.reader.remote.requests.WorkRequest
import org.eidos.reader.repository.paging.remote.AO3PagingSource
import org.eidos.reader.repository.paging.remote.WorkBlurbUiModelPagingSource
import org.eidos.reader.storage.Storage
import org.eidos.reader.ui.misc.preferences.UiPreferences
import org.eidos.reader.ui.misc.preferences.UiPreferencesKeys
import org.eidos.reader.workers.DownloadWorker
import timber.log.Timber
import java.io.IOException

/**
 * This repository/facade provides a unified API through which the ViewModel can interact with to
 * retrieve data.
 * All requests for data, be it local or remote, will be made through this repository and not
 * through the individual components (e.g. the AO3 class)
 *
 * This class is implemented as a singleton first (object keyword), but will have to be reworked to
 * adhere to proper dependency injection. Currently this class is not very testable as-is.
 */
class EidosRepository(
    private val remoteDataSource: AO3,
    private val localDataSource: Storage,
    val preferencesDataStore: DataStore<Preferences>,
    private val workManager: WorkManager
)
{
    // TODO: Implement local data source and cache (not as important)

    fun getWorkFromAO3(workRequest: WorkRequest): Work {
        return remoteDataSource.getWork(workRequest)
    }

    fun getWorkBlurbFromWorkAO3(workRequest: WorkRequest): WorkBlurb {
        return remoteDataSource.getWorkBlurbFromWork(workRequest)
    }

    fun getWorkBlurbsFromAO3(workFilterRequest: WorkFilterRequest): List<WorkBlurb> {
        return remoteDataSource.getWorkBlurbs(workFilterRequest)
    }

    private fun getWorkBlurbStreamFromAO3(workFilterRequest: WorkFilterRequest): Flow<PagingData<WorkBlurb>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20, // magic number, no difference
                enablePlaceholders = false,
            ),
            pagingSourceFactory = { AO3PagingSource(remoteDataSource, workFilterRequest) }
        ).flow
    }

    fun getWorkBlurbUiModelStreamFromAO3(workFilterRequest: WorkFilterRequest)
    : Flow<PagingData<WorkBlurbUiModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20, // magic number, no difference
                enablePlaceholders = false,
            ),
            pagingSourceFactory = { WorkBlurbUiModelPagingSource(remoteDataSource, workFilterRequest) }
        ).flow
    }

    fun getWorkSearchMetadataFromAO3(workFilterRequest: WorkFilterRequest): WorkSearchMetadata {
        return remoteDataSource.getWorkSearchMetadata(workFilterRequest)
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

    private fun getWorkBlurbStreamFromDatabase(): Flow<PagingData<SavedWorkBlurb>> {
        return Pager(
            config = PagingConfig(
                initialLoadSize = 20,
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { localDataSource.getSavedWorkBlurbsPagingSource() }
        ).flow
//            .map { pagingData -> pagingData.map { blurb -> blurb.toWorkBlurb() } }
    }

    fun getWorkBlurbFlowFromDatabase(): Flow<List<WorkBlurb>> {
        return localDataSource.savedWorkBlurbsFlow
    }

    fun getWorkFromDatabase(workURL: String): Work {
        return localDataSource.getWork(workURL)
    }

    fun insertWorkIntoDatabase(work: Work) {
        return localDataSource.insertWork(work)
    }

    fun insertWorkIntoDatabase(workURL: String) {
        workManager.enqueue(DownloadWorker.createDownloadRequest(workURL))
    }

    fun deleteWorkFromDatabase(workURL: String) {
        return localDataSource.deleteWork(workURL)
    }

    fun updateWorksInDatabase(updatedWorks: List<Work>) {
        return localDataSource.updateWorks(updatedWorks)
    }

    fun deleteAllWorksFromDatabase(workURL: String) {
        return localDataSource.deleteAllWorks()
    }



    /* Reading List */
    fun getWorkBlurbsFromReadingList(): List<WorkBlurb> {
        return localDataSource.getReadingList()
    }

    private fun getWorkBlurbStreamFromReadingList(): Flow<PagingData<ReadingListWorkBlurb>> {
        return Pager(
            config = PagingConfig(
                initialLoadSize = 20,
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { localDataSource.getReadingListWorkBlurbsPagingSource() }
        ).flow
//            .map { pagingData -> pagingData.map { blurb -> blurb.toWorkBlurb() } }
    }

    fun getReadingListFlow(): Flow<List<WorkBlurb>> {
        return localDataSource.readingListFlow
    }

    fun addWorkBlurbToReadingList(workBlurb: WorkBlurb) {
        return localDataSource.addWorkBlurbToReadingList(workBlurb)
    }

    fun removeWorkFromReadingList(workURL: String) {
        return localDataSource.deleteWorkBlurbFromReadingList(workURL)
    }

    /* Preferences */
    val uiPreferencesFlow: Flow<UiPreferences> = preferencesDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            Timber.i("UiPreferencesFlow Updated")
            val useNightMode = preferences[UiPreferencesKeys.USE_NIGHT_MODE] ?: false
            val readerTextSize = preferences[UiPreferencesKeys.READER_TEXT_SIZE] ?: 16F
            return@map UiPreferences(
                useNightMode = useNightMode,
                readerTextSize = readerTextSize
            )
        }

    suspend fun setNightMode(useNightMode: Boolean) {
        preferencesDataStore.edit { preferences ->
            preferences[UiPreferencesKeys.USE_NIGHT_MODE] = useNightMode
        }
    }

    suspend fun updateTextSize(textSize: Float) {
        preferencesDataStore.edit { preferences ->
            preferences[UiPreferencesKeys.READER_TEXT_SIZE] = textSize
        }
    }
}
