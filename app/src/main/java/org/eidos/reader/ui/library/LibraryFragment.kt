package org.eidos.reader.ui.library

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import org.eidos.reader.EidosApplication
import org.eidos.reader.R
import org.eidos.reader.container.AppContainer
import org.eidos.reader.databinding.FragmentLibraryBinding
import org.eidos.reader.ui.misc.adapters.WorkBlurbCompactAdapter
import org.eidos.reader.ui.misc.utilities.Utilities.Companion.hideKeyboard

class LibraryFragment : Fragment() {

    companion object {
        fun newInstance() = LibraryFragment()
    }

    private val viewModel: LibraryViewModel by viewModels {
        LibraryViewModelFactory(appContainer.repository)
    }

    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!

    private lateinit var appContainer: AppContainer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        appContainer = (requireActivity().application as EidosApplication).appContainer

        // TODO: structure roughly the same as worklist
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity).setupActionBarWithNavController(findNavController())

        val adapter = WorkBlurbCompactAdapter { holderView, workBlurb ->
            holderView.findNavController()
                .navigate(LibraryFragmentDirections
                    .actionLibraryFragmentToWorkReader(workBlurb.workURL, true))
        }

        binding.workListDisplay.adapter = adapter

        //get data into the adapter
        viewModel.workBlurbs.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.data = it
            }
        })
        // TODO: write code to listen to viewModel

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideKeyboard()
        _binding = null
    }

}