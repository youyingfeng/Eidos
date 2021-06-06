package org.eidos.reader.ui.browse.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.eidos.reader.repository.EidosRepository

class BrowseSearchViewModelFactory
    constructor(
        private val repository: EidosRepository
    )
    : ViewModelProvider.Factory
{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BrowseSearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BrowseSearchViewModel(repository) as T
        }
        throw IllegalArgumentException("Factory cannot make ViewModel of type ${modelClass.simpleName}")
    }
}