package org.eidos.reader.ui.browse.worklistfilter

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import org.eidos.reader.EidosApplication
import org.eidos.reader.container.AppContainer
import org.eidos.reader.databinding.FormWorkFilterBinding
import org.eidos.reader.remote.choices.WorkFilterChoices
import org.eidos.reader.ui.misc.utilities.Utilities.Companion.hideKeyboard
import org.eidos.reader.ui.browse.worklist.WorkListViewModel
import org.eidos.reader.ui.misc.adapters.AutocompleteStringAdapter
import timber.log.Timber

/*
TODO: In the future, add functionality to automatically add dashes to date fields,
and also stop additional typing of dashes from fucking up the date format
 */

class WorkListFilterFragment : Fragment() {

    // by viewModels kotlin delegate is basically a lazy initialiser
    // FIXME: cannot create instance of VM
    private val viewModel : WorkListViewModel by activityViewModels()
    private val workListFilterViewModel : WorkListFilterViewModel by viewModels {
        WorkListFilterViewModelFactory(
            appContainer.repository,
            viewModel.workFilterRequest.workFilterChoices.copy()
        )
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

        // Set autocomplete recyclerviews
        val includeTagsAutocompleteAdapter =
            AutocompleteStringAdapter { _, autocompleteResultString ->
                if (!workListFilterViewModel.workFilterChoices.includedTags.contains(autocompleteResultString)) {
                    // adds a new chip to the chipgroup if it does not already exist
                    val newChip = Chip(this.context)
                    newChip.text = autocompleteResultString
                    newChip.isCloseIconVisible = true
                    newChip.isClickable = true
                    binding.includedTagsChipGroup.addView(newChip, binding.includedTagsChipGroup.childCount)
                    workListFilterViewModel.workFilterChoices.includedTags.add(autocompleteResultString)
                    newChip.setOnCloseIconClickListener {
                        binding.includedTagsChipGroup.removeView(newChip)
                        workListFilterViewModel
                            .workFilterChoices
                            .includedTags
                            .remove(autocompleteResultString)
                    }
                }

                binding.includedTagsEditText.text?.clear()
            }
        binding.includedTagsAutocompleteRecyclerView.adapter = includeTagsAutocompleteAdapter

        val excludeTagsAutocompleteAdapter =
            AutocompleteStringAdapter { _, autocompleteResultString ->
                if (!workListFilterViewModel.workFilterChoices.excludedTags.contains(autocompleteResultString)) {
                    // adds a new chip to the chipgroup if it does not already exist
                    val newChip = Chip(this.context)
                    newChip.text = autocompleteResultString
                    newChip.isCloseIconVisible = true
                    newChip.isClickable = true
                    binding.excludedTagsChipGroup.addView(newChip, binding.excludedTagsChipGroup.childCount)
                    workListFilterViewModel.workFilterChoices.excludedTags.add(autocompleteResultString)
                    newChip.setOnCloseIconClickListener {
                        binding.excludedTagsChipGroup.removeView(newChip)
                        workListFilterViewModel
                            .workFilterChoices
                            .excludedTags
                            .remove(autocompleteResultString)
                    }
                }
                binding.excludedTagsEditText.text?.clear()
            }
        binding.excludedTagsAutocompleteRecyclerView.adapter = excludeTagsAutocompleteAdapter

        // add a chip when user presses enter - might not need this, can ignore user input
//        binding.includedTagsEditText.setOnEditorActionListener { v, actionId, event ->
//            when (actionId) {
//                EditorInfo.IME_ACTION_DONE -> {
//                    if (binding.includedTagsEditText.text?.isBlank() == false) {
//                        binding.includedTagsChipGroup.addChipWithText(binding.includedTagsEditText.text.toString())
//                    }
//                    binding.includedTagsEditText.text?.clear()
//                    return@setOnEditorActionListener true
//                }
//                else -> false
//            }
//        }
//
//        binding.excludedTagsEditText.setOnEditorActionListener { v, actionId, event ->
//            when (actionId) {
//                EditorInfo.IME_ACTION_DONE -> {
//                    if (binding.excludedTagsEditText.text?.isBlank() == false) {
//                        binding.excludedTagsChipGroup.addChipWithText(binding.excludedTagsEditText.text.toString())
//                    }
//                    binding.excludedTagsEditText.text?.clear()
//                    return@setOnEditorActionListener true
//                }
//                else -> false
//            }
//        }

        // bring up recyclerview on focus
        binding.includedTagsEditText.setOnFocusChangeListener { _, hasFocus: Boolean ->
            Timber.i("Include tags focus changed")
            if (hasFocus) {
                binding.includedTagsAutocompleteRecyclerView.visibility = View.VISIBLE
            } else {
                binding.includedTagsAutocompleteRecyclerView.visibility = View.GONE
                workListFilterViewModel.clearAutocompleteResults()
            }
        }

        binding.excludedTagsEditText.setOnFocusChangeListener { _, hasFocus: Boolean ->
            Timber.i("Exclude tags focus changed")
            if (hasFocus) {
                binding.excludedTagsAutocompleteRecyclerView.visibility = View.VISIBLE
            } else {
                binding.excludedTagsAutocompleteRecyclerView.visibility = View.GONE
                workListFilterViewModel.clearAutocompleteResults()
            }
        }

        // fetch results when user stops typing
        binding.includedTagsEditText.afterTextChangedDelayed(100L) { inputString ->
            if (inputString.isNotBlank()) {
                workListFilterViewModel.fetchAutocompleteResults(inputString)
            } else {
                workListFilterViewModel.clearAutocompleteResults()
            }
        }

        binding.excludedTagsEditText.afterTextChangedDelayed(100L) { inputString ->
            if (inputString.isNotBlank()) {
                workListFilterViewModel.fetchAutocompleteResults(inputString)
            } else {
                workListFilterViewModel.clearAutocompleteResults()
            }
        }

        // pump the results to the respective adapters
        workListFilterViewModel.autocompleteResults.observe(viewLifecycleOwner, {
            it?.let {
                includeTagsAutocompleteAdapter.data = it
                excludeTagsAutocompleteAdapter.data = it
//                includeTagsAutocompleteAdapter.notifyDataSetChanged()
//                excludeTagsAutocompleteAdapter.notifyDataSetChanged()
            }
        })


        // TODO: should also add a "Reset to Defaults" button? Maybe when in the original state?
        // TODO: or make the clear button throw a dialog - can choose which type of reset they want


        // mass observer assignment
        // holy shit so this is what they mean by unreadable xml code
        // Ratings
        binding.ratingGeneralChip.setOnCheckedChangeListener { _, isChecked ->
            workListFilterViewModel.workFilterChoices.showRatingGeneral = isChecked
        }
        binding.ratingTeenChip.setOnCheckedChangeListener { _, isChecked ->
            workListFilterViewModel.workFilterChoices.showRatingTeen = isChecked
        }
        binding.ratingMatureChip.setOnCheckedChangeListener { _, isChecked ->
            workListFilterViewModel.workFilterChoices.showRatingMature = isChecked
        }
        binding.ratingExplicitChip.setOnCheckedChangeListener { _, isChecked ->
            workListFilterViewModel.workFilterChoices.showRatingExplicit = isChecked
        }
        binding.ratingNotRatedChip.setOnCheckedChangeListener { _, isChecked ->
            workListFilterViewModel.workFilterChoices.showRatingNotRated = isChecked
        }

        // Warnings
        binding.warningCreatorChoseNoWarningsChip.setOnCheckedChangeListener { _, isChecked ->
            workListFilterViewModel.workFilterChoices.showWarningChoseNoWarnings = isChecked
        }
        binding.warningNoWarningsChip.setOnCheckedChangeListener { _, isChecked ->
            workListFilterViewModel.workFilterChoices.showWarningNone = isChecked
        }
        binding.warningMajorCharacterDeathChip.setOnCheckedChangeListener { _, isChecked ->
            workListFilterViewModel.workFilterChoices.showWarningCharacterDeath = isChecked
        }
        binding.warningRapeChip.setOnCheckedChangeListener { _, isChecked ->
            workListFilterViewModel.workFilterChoices.showWarningRape = isChecked
        }
        binding.warningUnderageChip.setOnCheckedChangeListener { _, isChecked ->
            workListFilterViewModel.workFilterChoices.showWarningUnderage = isChecked
        }
        binding.warningViolenceChip.setOnCheckedChangeListener { _, isChecked ->
            workListFilterViewModel.workFilterChoices.showWarningViolence = isChecked
        }
        binding.warningsIncludeAllSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            workListFilterViewModel.workFilterChoices.mustContainAllWarnings = isChecked
        }


        // Categories
        // TODO: check if this button should be here
//        binding.relationshipNotSpecifiedChip.setOnCheckedChangeListener { _, isChecked ->
//            workListFilterViewModel.workFilterChoices
//        }
        binding.relationshipGenChip.setOnCheckedChangeListener { _, isChecked ->
            workListFilterViewModel.workFilterChoices.showCategoryGen = isChecked
        }
        binding.relationshipFFChip.setOnCheckedChangeListener { _, isChecked ->
            workListFilterViewModel.workFilterChoices.showCategoryFF = isChecked
        }
        binding.relationshipFMChip.setOnCheckedChangeListener { _, isChecked ->
            workListFilterViewModel.workFilterChoices.showCategoryFM = isChecked
        }
        binding.relationshipMMChip.setOnCheckedChangeListener { _, isChecked ->
            workListFilterViewModel.workFilterChoices.showCategoryMM = isChecked
        }
        binding.relationshipMultiChip.setOnCheckedChangeListener { _, isChecked ->
            workListFilterViewModel.workFilterChoices.showCategoryMulti = isChecked
        }
        binding.relationshipOtherChip.setOnCheckedChangeListener { _, isChecked ->
            workListFilterViewModel.workFilterChoices.showCategoryOther = isChecked
        }

        binding.showCrossoversChip.setOnCheckedChangeListener { _, isChecked ->
            workListFilterViewModel.workFilterChoices.showCrossovers = isChecked
        }
        binding.showNonCrossoversChip.setOnCheckedChangeListener { _, isChecked ->
            workListFilterViewModel.workFilterChoices.showNonCrossovers = isChecked
        }

        binding.showCompletedChip.setOnCheckedChangeListener { _, isChecked ->
            workListFilterViewModel.workFilterChoices.showCompletedWorks = isChecked
        }
        binding.showIncompleteChip.setOnCheckedChangeListener { _, isChecked ->
            workListFilterViewModel.workFilterChoices.showIncompleteWorks = isChecked
        }

        binding.oneshotsOnlyToggle.setOnCheckedChangeListener { buttonView, isChecked ->
            workListFilterViewModel.workFilterChoices.showSingleChapterWorksOnly = isChecked
        }


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

//        choices.showCategoryGen = binding.relationshipGenChip.isChecked
//        choices.showCategoryFM = binding.relationshipFMChip.isChecked
//        choices.showCategoryMM = binding.relationshipMMChip.isChecked
//        choices.showCategoryFF = binding.relationshipFFChip.isChecked
//        choices.showCategoryMulti = binding.relationshipMultiChip.isChecked
//        choices.showCategoryOther = binding.relationshipOtherChip.isChecked
//        choices.showCrossovers = binding.showCrossoversChip.isChecked
//        choices.showNonCrossovers = binding.showNonCrossoversChip.isChecked
//        choices.showCompletedWorks = binding.showCompletedChip.isChecked
//        choices.showIncompleteWorks = binding.showIncompleteChip.isChecked
//        choices.hitsMin = binding.hitsFromEditText.text?.let { it ->
//            // TODO: should handle parseInt exception
//            if (it.isBlank()) 0 else Integer.parseInt(it.toString())
//        } ?: 0
//        choices.hitsMax = 0
//        choices.kudosMin = 0
//        choices.kudosMax = 0
//        choices.bookmarksMin = 0
//        choices.bookmarksMax = 0
//        choices.wordCountMin = 0
//        choices.wordCountMax = 0
//        choices.dateUpdatedMin = ""
//        choices.dateUpdatedMax = ""
//        choices.searchTerm = ""
//        choices.showSingleChapterWorksOnly = binding.oneshotsOnlyToggle.isActivated
//        choices.includedTags = emptyList()
//        choices.excludedTags = emptyList()
//        choices.language = ""

        // then pass this object to the request outside?
        // outside of the function, we resend the request
        return choices
    }

    private fun loadFormParameters() {
        // obtain the choices object from the request (need to update request contract too)
    }

    private fun ChipGroup.addChipWithText(text: String) {
        val newChip = Chip(this.context)
        newChip.text = text
        newChip.isCloseIconVisible = true
        newChip.isClickable = true
        this.addView(newChip, this.childCount)
        workListFilterViewModel.workFilterChoices.includedTags.add(text)
        newChip.setOnCloseIconClickListener {
            this.removeView(newChip)
            workListFilterViewModel.workFilterChoices.includedTags.remove(text)
        }
    }

    companion object {


        fun TextView.afterTextChangedDelayed(delayInMilliseconds: Long, afterTextChanged: (String) -> Unit) {
            this.addTextChangedListener(object : TextWatcher {
                var timer: CountDownTimer? = null

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

                override fun afterTextChanged(editable: Editable?) {
                    timer?.cancel()
                    timer = object : CountDownTimer(delayInMilliseconds, delayInMilliseconds) {
                        override fun onTick(millisUntilFinished: Long) {
                            // Don't care about onTick() as nothing needs to be done
                        }

                        override fun onFinish() {
                            afterTextChanged.invoke(editable.toString())
                        }
                    }.start()
                }
            })
        }
    }
}


