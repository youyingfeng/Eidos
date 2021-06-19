package org.eidos.reader.ui.read.workinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.eidos.reader.model.WorkBlurb

class WorkInfoViewModelFactory
    constructor(
        private val workBlurb: WorkBlurb
    )
    : ViewModelProvider.Factory
{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorkInfoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WorkInfoViewModel(workBlurb) as T
        }
        throw IllegalArgumentException("Factory cannot make ViewModel of type ${modelClass.simpleName}")
    }
}