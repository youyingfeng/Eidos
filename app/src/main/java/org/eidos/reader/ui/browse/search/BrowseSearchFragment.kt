package org.eidos.reader.ui.browse.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import org.eidos.reader.EidosApplication
import org.eidos.reader.R
import org.eidos.reader.WorkListDirections
import org.eidos.reader.container.AppContainer
import org.eidos.reader.databinding.FragmentBrowseSearchBinding
import org.eidos.reader.ui.misc.adapters.AutocompleteResultAdapter
import org.eidos.reader.ui.misc.utilities.Utilities.Companion.hideKeyboard

class BrowseSearchFragment : Fragment() {

    companion object {
        fun newInstance() = BrowseSearchFragment()
    }

    private val viewModel: BrowseSearchViewModel by viewModels {
        BrowseSearchViewModelFactory(appContainer.repository)
    }

    private var _binding: FragmentBrowseSearchBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var appContainer: AppContainer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBrowseSearchBinding.inflate(inflater, container, false)
        appContainer = (requireActivity().application as EidosApplication).appContainer

        val adapter = AutocompleteResultAdapter { holderView: View, autocompleteResultString: String ->
            hideKeyboard()

            if (binding.searchTypeChipGroup.checkedChipId != R.id.usersChip) {
                holderView.findNavController()
                    .navigate(WorkListDirections.actionBrowseTag(autocompleteResultString))
            } else {
                TODO("User page not implemented")
            }

        }

        binding.autocompleteResultsDisplay.adapter = adapter

        // get data into adapter
        viewModel.autocompleteResults.observe(viewLifecycleOwner) {
            it?.let {
                adapter.data = it
            }
        }

        binding.searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    viewModel.fetchAutocompleteResults(it, binding.searchTypeChipGroup.checkedChipId)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    viewModel.fetchAutocompleteResults(it, binding.searchTypeChipGroup.checkedChipId)
                }
                return true
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

            binding.searchView.query?.let {
                viewModel.fetchAutocompleteResults(it.toString(), binding.searchTypeChipGroup.checkedChipId)
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
