package org.eidos.reader.ui.read.readinglist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.eidos.reader.repository.EidosRepository

class ReadingListViewModelFactory
    constructor(private val repository: EidosRepository)
    : ViewModelProvider.Factory
{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReadingListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReadingListViewModel(repository) as T
        }
        throw IllegalArgumentException("Factory cannot make ViewModel of type ${modelClass.simpleName}")
    }
}
