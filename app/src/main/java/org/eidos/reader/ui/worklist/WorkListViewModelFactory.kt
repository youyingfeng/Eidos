package org.eidos.reader.ui.worklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.eidos.reader.remote.requests.WorkFilterRequest
import org.eidos.reader.ui.reader.WorkReaderViewModel

class WorkListViewModelFactory(
        private val workFilterRequest: WorkFilterRequest
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorkListViewModel::class.java)) {
            return WorkListViewModel(workFilterRequest) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}