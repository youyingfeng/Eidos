package org.eidos.reader.ui.browse.worklistfilter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import org.eidos.reader.EidosApplication
import org.eidos.reader.container.AppContainer
import org.eidos.reader.databinding.FormWorkFilterBinding
import org.eidos.reader.remote.choices.WorkFilterChoices
import org.eidos.reader.ui.misc.utilities.Utilities.Companion.hideKeyboard
import org.eidos.reader.ui.browse.worklist.WorkListViewModel

/*
TODO: In the future, add functionality to automatically add dashes to date fields,
and also stop additional typing of dashes from fucking up the date format
 */

class WorkListFilterFragment : Fragment() {

    // by viewModels kotlin delegate is basically a lazy initialiser
    // FIXME: cannot create instance of VM
    private val viewModel : WorkListViewModel by activityViewModels()
    private val workListFilterViewModel : WorkListFilterViewModel by viewModels {
        WorkListFilterViewModelFactory(appContainer.repository)
    }
    /* The AutocompleteViewModel is meant to provide autocomplete livedata only */

    private var _binding: FormWorkFilterBinding? = null
    val binding : FormWorkFilterBinding
        get() = _binding!!

    private lateinit var appContainer: AppContainer


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FormWorkFilterBinding.inflate(LayoutInflater.from(context))
        appContainer = (requireActivity().application as EidosApplication).appContainer

        // Automatically creates a back button in the toolbar to navigate back
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity).setupActionBarWithNavController(findNavController())

        loadFormParameters()

        binding.confirmButton.setOnClickListener {
            val choices = processFormInput()
            // TODO: update parent VM fields
        }

        binding.clearButton.setOnClickListener {
            loadFormParameters()
        }

        // TODO: should also add a "Reset to Defaults" button? Maybe when in the original state?
        // TODO: or make the clear button throw a dialog - can choose which type of reset they want

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideKeyboard()
        _binding = null
    }

    private fun processFormInput() : WorkFilterChoices {
        // create a new Choices object with the given parameters
        val choices = WorkFilterChoices()

        choices.showCategoryGen = binding.relationshipGenChip.isChecked
        choices.showCategoryFM = binding.relationshipFMChip.isChecked
        choices.showCategoryMM = binding.relationshipMMChip.isChecked
        choices.showCategoryFF = binding.relationshipFFChip.isChecked
        choices.showCategoryMulti = binding.relationshipMultiChip.isChecked
        choices.showCategoryOther = binding.relationshipOtherChip.isChecked
        choices.showCrossovers = binding.showCrossoversChip.isChecked
        choices.showNonCrossovers = binding.showNonCrossoversChip.isChecked
        choices.showCompletedWorks = binding.showCompletedChip.isChecked
        choices.showIncompleteWorks = binding.showIncompleteChip.isChecked
        choices.hitsMin = binding.hitsFromEditText.text?.let { it ->
            // TODO: should handle parseInt exception
            if (it.isBlank()) 0 else Integer.parseInt(it.toString())
        } ?: 0
        choices.hitsMax = 0
        choices.kudosMin = 0
        choices.kudosMax = 0
        choices.bookmarksMin = 0
        choices.bookmarksMax = 0
        choices.wordCountMin = 0
        choices.wordCountMax = 0
        choices.dateUpdatedMin = ""
        choices.dateUpdatedMax = ""
        choices.searchTerm = ""
        choices.showSingleChapterWorksOnly = binding.oneshotsOnlyToggle.isActivated
        choices.includedTags = emptyList()
        choices.excludedTags = emptyList()
        choices.language = ""

        // then pass this object to the request outside?
        // outside of the function, we resend the request
        return choices
    }

    private fun loadFormParameters() {
        // obtain the choices object from the request (need to update request contract too)
    }
}


