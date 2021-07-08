package org.eidos.reader.ui.library

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.eidos.reader.EidosApplication
import org.eidos.reader.WorkDirections
import org.eidos.reader.container.AppContainer
import org.eidos.reader.databinding.LocalWorkListBinding
import org.eidos.reader.ui.misc.adapters.WorkBlurbLocalAdapter
import org.eidos.reader.ui.misc.utilities.Utilities.Companion.hideKeyboard

class LibraryFragment : Fragment() {

    companion object {
        fun newInstance() = LibraryFragment()

        private val options = arrayOf("Remove Work from Library", "Add to Reading List")
    }

    private val viewModel: LibraryViewModel by viewModels {
        LibraryViewModelFactory(appContainer.repository)
    }

    private var _binding: LocalWorkListBinding? = null
    private val binding get() = _binding!!

    private lateinit var appContainer: AppContainer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LocalWorkListBinding.inflate(inflater, container, false)
        appContainer = (requireActivity().application as EidosApplication).appContainer

        // TODO: structure roughly the same as worklist
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity).setupActionBarWithNavController(findNavController())

        binding.tagNameTextView.text = "Library"

        val adapter = WorkBlurbLocalAdapter(
            { holderView, workBlurb ->
                holderView.findNavController()
                    .navigate(WorkDirections.actionShowWorkInfo(workBlurb, true))
            },
            { holderView, workBlurb ->
                MaterialAlertDialogBuilder(holderView.context)
                    .setTitle(workBlurb.title)
                    .setItems(options) { dialog, which ->
                        when(which) {
                            0 -> viewModel.deleteWorkFromLibrary(workBlurb)
                            1 -> viewModel.addWorkToReadingList(workBlurb)
                        }
                    }
                    .show()
            }
        )

        binding.workListDisplay.adapter = adapter

        // update the list upon every change in the list provided by the flow
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.workBlurbFlow.collectLatest {
                adapter.submitList(it)
                binding.workCountTextView.text = "Listing ${it.size} works"
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideKeyboard()
        _binding = null
    }
}
