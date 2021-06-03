package org.eidos.reader.ui.browse.worklistfilter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.eidos.reader.repository.EidosRepository

class WorkListFilterViewModelFactory
    constructor(
        private val repository: EidosRepository
    )
    : ViewModelProvider.Factory
{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorkListFilterViewModel::class.java)) {
            return WorkListFilterViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
