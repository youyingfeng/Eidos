package org.eidos.reader.ui.reader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class WorkReaderViewModelFactory(private val workURL: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorkReaderViewModel::class.java)) {
            return WorkReaderViewModel(workURL) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}