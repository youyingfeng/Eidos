package org.eidos.reader.ui.library

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.eidos.reader.model.domain.WorkBlurb
import org.eidos.reader.repository.EidosRepository

class LibraryViewModel(val repository: EidosRepository) : ViewModel() {
    // TODO: Implement the ViewModel

    // TODO: write code to retrieve works from DB

//    val workBlurbStream = repository.getWorkBlurbStreamFromDatabase()

    val workBlurbFlow = repository.getWorkBlurbFlowFromDatabase()

    fun deleteWorkFromLibrary(workBlurb: WorkBlurb) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.deleteWorkFromDatabase(workBlurb.workURL)
        }
    }

    fun addWorkToReadingList(savedWorkBlurb: WorkBlurb) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.addWorkBlurbToReadingList(savedWorkBlurb)
        }
    }
}
