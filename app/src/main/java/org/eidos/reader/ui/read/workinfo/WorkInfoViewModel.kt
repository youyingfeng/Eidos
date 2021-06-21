package org.eidos.reader.ui.read.workinfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.eidos.reader.model.WorkBlurb
import org.eidos.reader.remote.requests.WorkRequest
import org.eidos.reader.repository.EidosRepository

class WorkInfoViewModel
    constructor(
        val workBlurb: WorkBlurb,
        val isStoredInDatabase: Boolean,
        val repository: EidosRepository
    )
    : ViewModel()
{
    // this class is literally just here to store the workblurb
    // and also offer a few more methods
    // TODO: this might not even be needed! arguments should be persisted iirc

    fun addWorkToLibrary(workBlurb: WorkBlurb) {
        repository.insertWorkIntoDatabase(workBlurb.workURL)
    }

    fun addWorkToReadingList(workBlurb: WorkBlurb) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.addWorkBlurbToReadingList(workBlurb)
        }
    }
}