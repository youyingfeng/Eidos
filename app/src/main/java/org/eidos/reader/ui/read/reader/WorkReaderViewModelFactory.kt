package org.eidos.reader.ui.read.reader

import android.text.Html
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.eidos.reader.repository.EidosRepository

class WorkReaderViewModelFactory
    constructor(
        private val workURL: String,
        private val fetchFromDatabase: Boolean,
        private val repository: EidosRepository,
        private val htmlImageGetter: Html.ImageGetter
    )
    : ViewModelProvider.Factory
{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorkReaderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WorkReaderViewModel(workURL, fetchFromDatabase, repository, htmlImageGetter) as T
        }
        throw IllegalArgumentException("Factory cannot make ViewModel of type ${modelClass.simpleName}")
    }
}
