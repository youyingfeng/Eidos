package org.eidos.reader.workers

import android.content.Context
import androidx.work.*
import org.eidos.reader.EidosApplication
import org.eidos.reader.remote.requests.WorkRequest
import timber.log.Timber

class UpdateStoriesWorker
    constructor(
        context: Context,
        parameters: WorkerParameters
    )
    : Worker(context, parameters)
{
    override fun doWork(): Result {
        val appContext = applicationContext
        makeStatusNotification("Download in progress", "Downloading 1 work to Library", appContext)

        try {
            val repository = (appContext as EidosApplication).appContainer.repository
            val workBlurbs = repository.getWorkBlurbsFromDatabase()
            val updatedWorks = workBlurbs.mapNotNull { blurb ->
                // TODO: add proper error handling
                val work = repository.getWorkFromAO3(WorkRequest(blurb.workURL))
                if (blurb.isSimilarTo(work.getWorkBlurb())) {
                    return@mapNotNull work
                } else {
                    return@mapNotNull null
                }
            }.toList()

            repository.updateWorksInDatabase(updatedWorks)

            makeStatusNotification("Download complete", "1 work added to Library", appContext)
            return Result.success()
        } catch (throwable: Throwable) {
            Timber.e("You dun goofed")
            // TODO: add retry logic
            makeStatusNotification("Download failed", "An error has occurred", appContext)
            return Result.failure()
        }
    }

    companion object {
        private const val KEY_WORK_URLS = "WORK_URL"

        private fun createInputDataFromWorkURL(workURLs: Array<String>): Data {
            return Data.Builder()
                .putStringArray(KEY_WORK_URLS, workURLs)
                .build()
        }

        // TODO: this should not occur at the same time as download
        fun createDownloadRequest(workURLs: Array<String>): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<UpdateStoriesWorker>()
                .setInputData(createInputDataFromWorkURL(workURLs))
                .build()
        }
    }
}