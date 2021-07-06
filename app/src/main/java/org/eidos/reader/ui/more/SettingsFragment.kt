package org.eidos.reader.ui.more

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import org.eidos.reader.EidosApplication
import org.eidos.reader.R
import org.eidos.reader.datastore.SettingsDataStore
import org.eidos.reader.repository.EidosRepository

class SettingsFragment : PreferenceFragmentCompat() {

    lateinit var repository: EidosRepository

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        repository = (requireActivity().application as EidosApplication).appContainer.repository
        preferenceManager.preferenceDataStore = SettingsDataStore(repository.preferencesDataStore)
        setPreferencesFromResource(R.xml.preferences, null) // null for now
    }
}