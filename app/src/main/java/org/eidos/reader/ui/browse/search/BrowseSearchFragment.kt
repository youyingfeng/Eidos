package org.eidos.reader.ui.browse.search

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.eidos.reader.R
import org.eidos.reader.databinding.FragmentBrowseSearchBinding
import org.eidos.reader.databinding.FragmentFandomTypeSelectionBinding
import org.eidos.reader.remote.requests.*
import org.eidos.reader.ui.autocomplete.AutocompleteStringAdapter
import org.eidos.reader.ui.utilities.Utilities.Companion.hideKeyboard

class BrowseSearchFragment : Fragment() {

    companion object {
        fun newInstance() = BrowseSearchFragment()
    }

    private val viewModel: BrowseSearchViewModel by viewModels()

    private var _binding: FragmentBrowseSearchBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    // FIXME: should I dump this in ViewModel? Technically I don't do much processing on this.
    private lateinit var queryTextChangedJob: Job

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBrowseSearchBinding.inflate(inflater, container, false)

        val adapter = AutocompleteStringAdapter { holderView: View, autocompleteResultString: String ->
            hideKeyboard()

            if (binding.searchTypeChipGroup.checkedChipId != R.id.usersChip) {
                holderView.findNavController()
                    .navigate(
                        BrowseSearchFragmentDirections
                            .actionBrowseSearchFragmentToWorkListFragment(autocompleteResultString))
            } else {
                TODO("User page not implemented")
            }

        }
        binding.autocompleteResultsDisplay.adapter = adapter

        // get data into adapter
        viewModel.autocompleteResults.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.data = it
            }
        })

        binding.searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (::queryTextChangedJob.isInitialized) {
                    queryTextChangedJob.cancel()
                }

                queryTextChangedJob = lifecycleScope.launch(Dispatchers.IO) {
                    delay(100L)
                    query?.let { viewModel.fetchAutocompleteResults(it,
                        binding.searchTypeChipGroup.checkedChipId) }
                }

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (::queryTextChangedJob.isInitialized) {
                    queryTextChangedJob.cancel()
                }

                queryTextChangedJob = lifecycleScope.launch(Dispatchers.IO) {
                    delay(100L)
                    newText?.let { viewModel.fetchAutocompleteResults(it,
                        binding.searchTypeChipGroup.checkedChipId) }
                }

                return false
            }

        })

        // update UI elements and results when a new chip is clicked
        binding.searchTypeChipGroup.setOnCheckedChangeListener { group, checkedId ->
            // Updates the hint of the search bar to match the current selected chip
            when (checkedId) {
                R.id.allTagsChip -> binding.searchView.queryHint = "Search all tags"
                R.id.fandomsChip -> binding.searchView.queryHint = "Search for fandoms"
                R.id.charactersChip -> binding.searchView.queryHint = "Search for characters"
                R.id.relationshipsChip -> binding.searchView.queryHint = "Search for relationships"
                R.id.freeformsChip -> binding.searchView.queryHint = "Search for freeform tags"
                R.id.usersChip -> binding.searchView.queryHint = "Search for users"
            }

            // Fetches new results from the repository
            if (::queryTextChangedJob.isInitialized) {
                queryTextChangedJob.cancel()
            }

            queryTextChangedJob = lifecycleScope.launch(Dispatchers.IO) {
                delay(100L)
                binding.searchView.query?.let { viewModel.fetchAutocompleteResults(it.toString(),
                    binding.searchTypeChipGroup.checkedChipId) }
            }
        }

        return binding.root
    }

}