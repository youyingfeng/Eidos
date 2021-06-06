package org.eidos.reader.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.eidos.reader.repository.EidosRepository

class LibraryViewModelFactory
    constructor(private val repository: EidosRepository)
    : ViewModelProvider.Factory
{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LibraryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LibraryViewModel(repository) as T
        }
        throw IllegalArgumentException("Factory cannot make ViewModel of type ${modelClass.simpleName}")
    }
}
