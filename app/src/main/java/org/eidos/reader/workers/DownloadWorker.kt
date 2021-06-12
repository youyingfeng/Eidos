package org.eidos.reader.workers

import android.content.Context
import androidx.work.*
import org.eidos.reader.EidosApplication
import org.eidos.reader.remote.requests.WorkRequest
import timber.log.Timber

class DownloadWorker
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
            val workURL = inputData.getString(KEY_WORK_URL)!!
            val repository = (appContext as EidosApplication).appContainer.repository

            val work = repository.getWorkFromAO3(WorkRequest(workURL))
            repository.insertWorkIntoDatabase(work)

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
        private const val KEY_WORK_URL = "WORK_URL"

        private fun createInputDataFromWorkURL(workURL: String): Data {
            return Data.Builder()
                .putString(KEY_WORK_URL, workURL)
                .build()
        }

        fun createDownloadRequest(workURL: String): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<DownloadWorker>()
                .setInputData(createInputDataFromWorkURL(workURL))
                .build()
        }
    }
}