package org.eidos.reader.ui.fandoms

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.eidos.reader.R

class FandomTypeSelectionFragment : Fragment() {

    companion object {
        fun newInstance() = FandomTypeSelectionFragment()
    }

    private lateinit var viewModel: FandomTypeSelectionViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_fandom_type_selection, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(FandomTypeSelectionViewModel::class.java)
        // TODO: Use the ViewModel
    }

}