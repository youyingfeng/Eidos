package org.eidos.reader.ui.browse.worklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.eidos.reader.remote.requests.WorkFilterRequest
import org.eidos.reader.repository.EidosRepository

class WorkListViewModelFactory
    constructor(
        private val workFilterRequest: WorkFilterRequest,
        private val repository: EidosRepository
    )
: ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorkListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WorkListViewModel(workFilterRequest, repository) as T
        }
        throw IllegalArgumentException("Factory cannot make ViewModel of type ${modelClass.simpleName}")
    }
}