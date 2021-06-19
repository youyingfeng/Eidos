package org.eidos.reader.ui.read.workinfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.eidos.reader.model.WorkBlurb

class WorkInfoViewModel(val workBlurb: WorkBlurb) : ViewModel() {
    // this class is literally just here to store the workblurb
    // and also offer a few more methods
    // TODO: this might not even be needed! arguments should be persisted iirc.
}