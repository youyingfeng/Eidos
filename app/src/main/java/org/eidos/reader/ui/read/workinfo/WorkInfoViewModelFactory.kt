package org.eidos.reader.ui.read.workinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.eidos.reader.model.domain.WorkBlurb
import org.eidos.reader.repository.EidosRepository

class WorkInfoViewModelFactory
    constructor(
        private val workBlurb: WorkBlurb,
        private val isStoredInDatabase: Boolean,
        private val repository: EidosRepository
    )
    : ViewModelProvider.Factory
{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorkInfoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WorkInfoViewModel(workBlurb, isStoredInDatabase, repository) as T
        }
        throw IllegalArgumentException("Factory cannot make ViewModel of type ${modelClass.simpleName}")
    }
}