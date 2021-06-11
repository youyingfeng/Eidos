package org.eidos.reader.ui.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.sqldelight.ColumnAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.eidos.reader.model.Chapter
import org.eidos.reader.model.WorkBlurb
import org.eidos.reader.repository.EidosRepository
import timber.log.Timber

class LibraryViewModel(val repository: EidosRepository) : ViewModel() {
    // TODO: Implement the ViewModel

    // TODO: write code to retrieve works from DB

    private var _workBlurbs = MutableLiveData<List<WorkBlurb>>(emptyList())
    val workBlurbs: LiveData<List<WorkBlurb>>
        get() = _workBlurbs

    init {
        fetchWorkBlurbsFromDatabase()
        Timber.i("init complete")
    }

    fun fetchWorkBlurbsFromDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            _workBlurbs.postValue(repository.getWorkBlurbsFromDatabase())
            Timber.i("WorkBlurbs successfully fetched from DB")
        }
    }

    fun deleteWorkFromLibrary(workBlurb: WorkBlurb) {
        repository.deleteWorkFromDatabase(workBlurb.workURL)
    }

    fun addWorkToReadingList(workBlurb: WorkBlurb) {
        repository.addWorkBlurbToReadingList(workBlurb)
    }
}