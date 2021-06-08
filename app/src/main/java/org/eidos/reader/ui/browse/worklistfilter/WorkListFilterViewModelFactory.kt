package org.eidos.reader.ui.browse.worklistfilter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.eidos.reader.remote.choices.WorkFilterChoices
import org.eidos.reader.repository.EidosRepository

class WorkListFilterViewModelFactory
    constructor(
        private val repository: EidosRepository,
        private val workFilterChoices: WorkFilterChoices
    )
    : ViewModelProvider.Factory
{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorkListFilterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WorkListFilterViewModel(repository, workFilterChoices) as T
        }
        throw IllegalArgumentException("Factory cannot make ViewModel of type ${modelClass.simpleName}")
    }
}
