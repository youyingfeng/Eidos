package org.eidos.reader.ui.read.readinglist

import androidx.lifecycle.ViewModelProvider
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
import org.eidos.reader.R
import org.eidos.reader.WorkDirections
import org.eidos.reader.container.AppContainer
import org.eidos.reader.databinding.FragmentReadingListBinding
import org.eidos.reader.databinding.LocalWorkListBinding
import org.eidos.reader.ui.library.LibraryFragment
import org.eidos.reader.ui.misc.adapters.WorkBlurbCompactAdapter
import org.eidos.reader.ui.misc.utilities.Utilities.Companion.hideKeyboard

class ReadingListFragment : Fragment() {

    companion object {
        fun newInstance() = ReadingListFragment()

        private val options = arrayOf("Add work to Library", "Remove from Reading List")
    }

    private val viewModel: ReadingListViewModel by viewModels {
        ReadingListViewModelFactory(appContainer.repository)
    }

    private var _binding: LocalWorkListBinding? = null
    private val binding get() = _binding!!

    private lateinit var appContainer: AppContainer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = LocalWorkListBinding.inflate(inflater, container, false)
        appContainer = (requireActivity().application as EidosApplication).appContainer
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity).setupActionBarWithNavController(findNavController())

        val adapter = WorkBlurbCompactAdapter(
            { holderView, workBlurb ->
                holderView.findNavController()
                    .navigate(WorkDirections.actionShowWorkInfo(workBlurb, true))
            },
            { holderView, workBlurb ->
                MaterialAlertDialogBuilder(holderView.context)
                    .setTitle(workBlurb.title)
                    .setItems(options) { dialog, which ->
                        when(which) {
                            0 -> viewModel.addWorkToLibrary(workBlurb)
                            1 -> viewModel.removeWorkFromReadingList(workBlurb)
                        }
                    }
                    .show()
            }
        )

        binding.workListDisplay.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.readingListFlow.collectLatest {
                adapter.submitList(it)
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
