package org.eidos.reader.ui.read.reader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.eidos.reader.repository.EidosRepository

class WorkReaderViewModelFactory
    constructor(
        private val workURL: String,
        private val fetchFromDatabase: Boolean,
        private val repository: EidosRepository
    )
    : ViewModelProvider.Factory
{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorkReaderViewModel::class.java)) {
            return WorkReaderViewModel(workURL, fetchFromDatabase, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
