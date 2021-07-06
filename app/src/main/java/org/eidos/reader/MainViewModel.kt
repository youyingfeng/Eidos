package org.eidos.reader

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.map
import org.eidos.reader.repository.EidosRepository

class MainViewModel(val repository: EidosRepository) : ViewModel() {

    // this info is presented as livedata in the VM instead of using the flow directly in the
    // activity, as the UI needs to be updated and crashes might occur.
    // see https://developer.android.com/kotlin/flow/stateflow-and-sharedflow warning
    val useNightMode: LiveData<Boolean> = repository.uiPreferencesFlow.map {
        return@map it.useNightMode
    }.asLiveData()
}