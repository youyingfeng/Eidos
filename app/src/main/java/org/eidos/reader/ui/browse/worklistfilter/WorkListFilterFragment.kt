package org.eidos.reader.ui.browse.worklistfilter

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.chip.Chip
import org.eidos.reader.EidosApplication
import org.eidos.reader.R
import org.eidos.reader.container.AppContainer
import org.eidos.reader.databinding.FormWorkFilterBinding
import org.eidos.reader.remote.choices.WorkFilterChoices
import org.eidos.reader.ui.misc.utilities.Utilities.Companion.hideKeyboard
import org.eidos.reader.ui.misc.adapters.AutocompleteResultAdapter
import org.eidos.reader.ui.misc.values.LANGUAGES_ARRAY
import org.eidos.reader.ui.misc.values.SORT_OPTIONS_ARRAY
import timber.log.Timber

/*
TODO: In the future, add functionality to automatically add dashes to date fields,
and also stop additional typing of dashes from fucking up the date format
 */

class WorkListFilterFragment : Fragment() {

//    private val workListViewModel : WorkListViewModel by activityViewModels()
    private val viewModel : WorkListFilterViewModel by viewModels {
        WorkListFilterViewModelFactory(
            appContainer.repository,
            workFilterChoices
        )
    }

    private var _binding: FormWorkFilterBinding? = null
    val binding : FormWorkFilterBinding
        get() = _binding!!

    private lateinit var appContainer: AppContainer
    private lateinit var workFilterChoices: WorkFilterChoices

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appContainer = (requireActivity().application as EidosApplication).appContainer

        val args = WorkListFilterFragmentArgs.fromBundle(requireArguments())
        workFilterChoices = args.workFilterChoices
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FormWorkFilterBinding.inflate(LayoutInflater.from(context))

        // Automatically creates a back button in the toolbar to navigate back
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity).setupActionBarWithNavController(findNavController())

        loadFormParameters()

        // Set autocomplete recyclerviews
        val includeTagsAutocompleteAdapter =
            AutocompleteResultAdapter { _, autocompleteResultString ->
                if (!viewModel.workFilterChoices.includedTags.contains(autocompleteResultString)) {
                    // adds a new chip to the chipgroup if it does not already exist
                    val newChip = Chip(this.context)
                    newChip.text = autocompleteResultString
                    newChip.isCloseIconVisible = true
                    newChip.isClickable = true
                    binding.includedTagsChipGroup.addView(newChip, binding.includedTagsChipGroup.childCount)
                    viewModel.workFilterChoices.includedTags.add(autocompleteResultString)
                    newChip.setOnCloseIconClickListener {
                        binding.includedTagsChipGroup.removeView(newChip)
                        viewModel
                            .workFilterChoices
                            .includedTags
                            .remove(autocompleteResultString)
                    }
                }

                binding.includedTagsEditText.text?.clear()
            }
        binding.includedTagsAutocompleteRecyclerView.adapter = includeTagsAutocompleteAdapter

        val excludeTagsAutocompleteAdapter =
            AutocompleteResultAdapter { _, autocompleteResultString ->
                if (!viewModel.workFilterChoices.excludedTags.contains(autocompleteResultString)) {
                    // adds a new chip to the chipgroup if it does not already exist
                    val newChip = Chip(this.context)
                    newChip.text = autocompleteResultString
                    newChip.isCloseIconVisible = true
                    newChip.isClickable = true
                    binding.excludedTagsChipGroup.addView(newChip, binding.excludedTagsChipGroup.childCount)
                    viewModel.workFilterChoices.excludedTags.add(autocompleteResultString)
                    newChip.setOnCloseIconClickListener {
                        binding.excludedTagsChipGroup.removeView(newChip)
                        viewModel
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
                viewModel.clearAutocompleteResults()
            }
        }

        binding.excludedTagsEditText.setOnFocusChangeListener { _, hasFocus: Boolean ->
            Timber.i("Exclude tags focus changed")
            if (hasFocus) {
                binding.excludedTagsAutocompleteRecyclerView.visibility = View.VISIBLE
            } else {
                binding.excludedTagsAutocompleteRecyclerView.visibility = View.GONE
                viewModel.clearAutocompleteResults()
            }
        }

        // fetch results when user stops typing
        binding.includedTagsEditText.afterTextChangedDelayed(100L) { inputString ->
            if (inputString.isNotBlank()) {
                viewModel.fetchAutocompleteResults(inputString)
            } else {
                viewModel.clearAutocompleteResults()
            }
        }

        binding.excludedTagsEditText.afterTextChangedDelayed(100L) { inputString ->
            if (inputString.isNotBlank()) {
                viewModel.fetchAutocompleteResults(inputString)
            } else {
                viewModel.clearAutocompleteResults()
            }
        }

        // pump the results to the respective adapters
        viewModel.autocompleteResults.observe(viewLifecycleOwner, {
            it?.let {
                includeTagsAutocompleteAdapter.data = it
                excludeTagsAutocompleteAdapter.data = it
//                includeTagsAutocompleteAdapter.notifyDataSetChanged()
//                excludeTagsAutocompleteAdapter.notifyDataSetChanged()
            }
        })

        // get data into the dropdown menus
        val languageAdapter = ArrayAdapter(requireContext(), R.layout.list_item, LANGUAGES_ARRAY)
        binding.languageSelection.setAdapter(languageAdapter)

        val sortOrderAdapter = ArrayAdapter(requireContext(), R.layout.list_item, SORT_OPTIONS_ARRAY)
        binding.sortingSelection.setAdapter(sortOrderAdapter)



        // TODO: should also add a "Reset to Defaults" button? Maybe when in the original state?
        // TODO: or make the clear button throw a dialog - can choose which type of reset they want


        // mass observer assignment
        // holy shit so this is what they mean by unreadable xml code
        // Ratings
        binding.ratingGeneralChip.setOnCheckedChangeListener { _, isChecked ->
            viewModel.workFilterChoices.showRatingGeneral = isChecked
        }
        binding.ratingTeenChip.setOnCheckedChangeListener { _, isChecked ->
            viewModel.workFilterChoices.showRatingTeen = isChecked
        }
        binding.ratingMatureChip.setOnCheckedChangeListener { _, isChecked ->
            viewModel.workFilterChoices.showRatingMature = isChecked
        }
        binding.ratingExplicitChip.setOnCheckedChangeListener { _, isChecked ->
            viewModel.workFilterChoices.showRatingExplicit = isChecked
        }
        binding.ratingNotRatedChip.setOnCheckedChangeListener { _, isChecked ->
            viewModel.workFilterChoices.showRatingNotRated = isChecked
        }

        // Warnings
        binding.warningCreatorChoseNoWarningsChip.setOnCheckedChangeListener { _, isChecked ->
            viewModel.workFilterChoices.showWarningChoseNoWarnings = isChecked
        }
        binding.warningNoWarningsChip.setOnCheckedChangeListener { _, isChecked ->
            viewModel.workFilterChoices.showWarningNone = isChecked
        }
        binding.warningMajorCharacterDeathChip.setOnCheckedChangeListener { _, isChecked ->
            viewModel.workFilterChoices.showWarningCharacterDeath = isChecked
        }
        binding.warningRapeChip.setOnCheckedChangeListener { _, isChecked ->
            viewModel.workFilterChoices.showWarningRape = isChecked
        }
        binding.warningUnderageChip.setOnCheckedChangeListener { _, isChecked ->
            viewModel.workFilterChoices.showWarningUnderage = isChecked
        }
        binding.warningViolenceChip.setOnCheckedChangeListener { _, isChecked ->
            viewModel.workFilterChoices.showWarningViolence = isChecked
        }
        binding.warningsIncludeAllSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.workFilterChoices.mustContainAllWarnings = isChecked
        }


        // Categories
        // TODO: check if this button should be here
//        binding.relationshipNotSpecifiedChip.setOnCheckedChangeListener { _, isChecked ->
//            workListFilterViewModel.workFilterChoices
//        }
        binding.relationshipGenChip.setOnCheckedChangeListener { _, isChecked ->
            viewModel.workFilterChoices.showCategoryGen = isChecked
            Timber.i("Choices Updated!")
        }
        binding.relationshipFFChip.setOnCheckedChangeListener { _, isChecked ->
            viewModel.workFilterChoices.showCategoryFF = isChecked
            Timber.i("Choices Updated!")
        }
        binding.relationshipFMChip.setOnCheckedChangeListener { _, isChecked ->
            viewModel.workFilterChoices.showCategoryFM = isChecked
            Timber.i("Choices Updated!")
        }
        binding.relationshipMMChip.setOnCheckedChangeListener { _, isChecked ->
            viewModel.workFilterChoices.showCategoryMM = isChecked
            Timber.i("Choices Updated!")
        }
        binding.relationshipMultiChip.setOnCheckedChangeListener { _, isChecked ->
            viewModel.workFilterChoices.showCategoryMulti = isChecked
            Timber.i("Choices Updated!")
        }
        binding.relationshipOtherChip.setOnCheckedChangeListener { _, isChecked ->
            viewModel.workFilterChoices.showCategoryOther = isChecked
            Timber.i("Choices Updated!")
        }

        binding.showCrossoversChip.setOnCheckedChangeListener { _, isChecked ->
            viewModel.workFilterChoices.showCrossovers = isChecked
            Timber.i("Choices Updated!")
        }
        binding.showNonCrossoversChip.setOnCheckedChangeListener { _, isChecked ->
            viewModel.workFilterChoices.showNonCrossovers = isChecked
            Timber.i("Choices Updated!")
        }

        binding.showCompletedChip.setOnCheckedChangeListener { _, isChecked ->
            viewModel.workFilterChoices.showCompletedWorks = isChecked
            Timber.i("Choices Updated!")
        }
        binding.showIncompleteChip.setOnCheckedChangeListener { _, isChecked ->
            viewModel.workFilterChoices.showIncompleteWorks = isChecked
            Timber.i("Choices Updated!")
        }

        binding.oneshotsOnlyToggle.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.workFilterChoices.showSingleChapterWorksOnly = isChecked
            Timber.i("Choices Updated!")
        }


        // holy shit edittext cancer
        // this entire code is listener hell
        binding.hitsFromEditText.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                viewModel.workFilterChoices.hitsMin = (view as EditText).text.toString().let {
                    if (it.isBlank()) 0 else it.toInt()
                }
                Timber.i("Choices Updated!")
            }
        }
        binding.hitsToEditText.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                viewModel.workFilterChoices.hitsMax = (view as EditText).text.toString().let {
                    if (it.isBlank()) 0 else it.toInt()
                }
                Timber.i("Choices Updated!")
            }
        }
        binding.kudosFromEditText.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                viewModel.workFilterChoices.kudosMin = (view as EditText).text.toString().let {
                    if (it.isBlank()) 0 else it.toInt()
                }
                Timber.i("Choices Updated!")
            }
        }
        binding.kudosToEditText.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                viewModel.workFilterChoices.kudosMax = (view as EditText).text.toString().let {
                    if (it.isBlank()) 0 else it.toInt()
                }
                Timber.i("Choices Updated!")
            }
        }
        binding.bookmarksFromEditText.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                viewModel.workFilterChoices.bookmarksMin = (view as EditText).text.toString().let {
                    if (it.isBlank()) 0 else it.toInt()
                }
                Timber.i("Choices Updated!")
            }
        }
        binding.bookmarksToEditText.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                viewModel.workFilterChoices.bookmarksMax = (view as EditText).text.toString().let {
                    if (it.isBlank()) 0 else it.toInt()
                }
                Timber.i("Choices Updated!")
            }
        }
        binding.commentsFromEditText.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                viewModel.workFilterChoices.commentsMin = (view as EditText).text.toString().let {
                    if (it.isBlank()) 0 else it.toInt()
                }
                Timber.i("Choices Updated!")
            }
        }
        binding.commentsToEditText.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                viewModel.workFilterChoices.commentsMax = (view as EditText).text.toString().let {
                    if (it.isBlank()) 0 else it.toInt()
                }
                Timber.i("Choices Updated!")
            }
        }
        binding.wordCountFromEditText.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                viewModel.workFilterChoices.wordCountMin = (view as EditText).text.toString().let {
                    if (it.isBlank()) 0 else it.toInt()
                }
                Timber.i("Choices Updated!")
            }
        }
        binding.wordCountToEditText.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                viewModel.workFilterChoices.wordCountMax = (view as EditText).text.toString().let {
                    if (it.isBlank()) 0 else it.toInt()
                }
                Timber.i("Choices Updated!")
            }
        }
        binding.dateUpdatedFromEditText.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                viewModel.workFilterChoices.dateUpdatedMin = (view as EditText).text.toString()
                Timber.i("Choices Updated!")
            }
        }
        binding.dateUpdatedToEditText.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                viewModel.workFilterChoices.dateUpdatedMax = (view as EditText).text.toString()
                Timber.i("Choices Updated!")
            }
        }

        binding.searchInputEditText.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                viewModel.workFilterChoices.searchTerm = (view as EditText).text.toString()
                Timber.i("Choices Updated!")
            }
        }

        binding.sortingSelection.setOnItemClickListener { parent, view, position, id ->
            viewModel.workFilterChoices.sortOrder = (view as TextView).text.toString()
            Timber.i("Choices Updated!")
        }

        binding.languageSelection.setOnItemClickListener { parent, view, position, id ->
            viewModel.workFilterChoices.language = (view as TextView).text.toString()
            Timber.i("Choices Updated!")
        }

        // pre-select options for language and sort order
        binding.sortingSelection.setText(SORT_OPTIONS_ARRAY[3], false)  // hardcode to date updated
        binding.languageSelection.setText(LANGUAGES_ARRAY[0], false)

        // TODO: missing observers for sort order and language

        // TODO: link submit and cancel buttons, add reset button for both types of reset
        binding.confirmButton.setOnClickListener {
            // send filter choices back to worklist fragment
            setFragmentResult(
                "updatedFilterChoices",
                bundleOf("workFilterChoices" to viewModel.workFilterChoices)
            )

            // navigate back
            findNavController().popBackStack()
        }

        binding.clearButton.setOnClickListener {
            // reset choices to defaults
            viewModel.resetChoicesToDefault()
            loadFormParameters()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideKeyboard()
        _binding = null
    }


    private fun loadFormParameters() {
        with (viewModel.workFilterChoices) {
            // FIXME: missing fields - sorting order, language
            binding.ratingGeneralChip.isChecked = showRatingGeneral
            binding.ratingTeenChip.isChecked = showRatingTeen
            binding.ratingMatureChip.isChecked = showRatingMature
            binding.ratingExplicitChip.isChecked = showRatingExplicit
            binding.ratingNotRatedChip.isChecked = showRatingNotRated

            binding.warningNoWarningsChip.isChecked = showWarningNone
            binding.warningViolenceChip.isChecked = showWarningViolence
            binding.warningRapeChip.isChecked = showWarningRape
            binding.warningUnderageChip.isChecked = showWarningUnderage
            binding.warningMajorCharacterDeathChip.isChecked = showWarningCharacterDeath
            binding.warningCreatorChoseNoWarningsChip.isChecked = showWarningChoseNoWarnings
            binding.warningsIncludeAllSwitch.isChecked = mustContainAllWarnings

            binding.relationshipGenChip.isChecked = showCategoryGen
            binding.relationshipFFChip.isChecked = showCategoryFF
            binding.relationshipFMChip.isChecked = showCategoryFM
            binding.relationshipMMChip.isChecked = showCategoryMM
            binding.relationshipMultiChip.isChecked = showCategoryMulti
            binding.relationshipOtherChip.isChecked = showCategoryOther

            includedTags.forEach { tag ->
                if (!viewModel.workFilterChoices.includedTags.contains(tag)) {
                    // adds a new chip to the chipgroup if it does not already exist
                    val newChip = Chip(context)
                    newChip.text = tag
                    newChip.isCloseIconVisible = true
                    newChip.isClickable = true
                    binding.includedTagsChipGroup.addView(newChip, binding.includedTagsChipGroup.childCount)
                    viewModel.workFilterChoices.includedTags.add(tag)
                    newChip.setOnCloseIconClickListener {
                        binding.includedTagsChipGroup.removeView(newChip)
                        viewModel
                            .workFilterChoices
                            .includedTags
                            .remove(tag)
                    }
                }
            }
            
            excludedTags.forEach { tag ->
                if (!viewModel.workFilterChoices.excludedTags.contains(tag)) {
                    // adds a new chip to the chipgroup if it does not already exist
                    val newChip = Chip(context)
                    newChip.text = tag
                    newChip.isCloseIconVisible = true
                    newChip.isClickable = true
                    binding.excludedTagsChipGroup.addView(newChip, binding.excludedTagsChipGroup.childCount)
                    viewModel.workFilterChoices.excludedTags.add(tag)
                    newChip.setOnCloseIconClickListener {
                        binding.excludedTagsChipGroup.removeView(newChip)
                        viewModel
                            .workFilterChoices
                            .excludedTags
                            .remove(tag)
                    }
                }
            }

            binding.showCompletedChip.isChecked = showCompletedWorks
            binding.showIncompleteChip.isChecked = showIncompleteWorks
            binding.showCrossoversChip.isChecked = showCrossovers
            binding.showNonCrossoversChip.isChecked = showNonCrossovers

            binding.hitsFromEditText.setText(if (hitsMin > 0) hitsMin.toString() else "")
            binding.hitsToEditText.setText(if (hitsMax > 0) hitsMax.toString() else "")
            binding.kudosFromEditText.setText(if (kudosMin > 0) kudosMin.toString() else "")
            binding.kudosToEditText.setText(if (kudosMax > 0) kudosMax.toString() else "")
            binding.bookmarksFromEditText.setText(if (bookmarksMin > 0) bookmarksMin.toString() else "")
            binding.bookmarksToEditText.setText(if (bookmarksMax > 0) bookmarksMax.toString() else "")
            binding.commentsFromEditText.setText(if (commentsMin > 0) commentsMin.toString() else "")
            binding.commentsToEditText.setText(if (commentsMax > 0) commentsMax.toString() else "")
            binding.wordCountFromEditText.setText(if (wordCountMin > 0) wordCountMin.toString() else "")
            binding.wordCountToEditText.setText(if (wordCountMax > 0) wordCountMax.toString() else "")
            binding.dateUpdatedFromEditText.setText(dateUpdatedMin)
            binding.dateUpdatedToEditText.setText(dateUpdatedMax)

            binding.oneshotsOnlyToggle.isChecked = showSingleChapterWorksOnly

            binding.searchInputEditText.setText(searchTerm)
        }
    }



    companion object {
        // commented for archival purposes
//        private fun ChipGroup.addChipWithText(text: String) {
//            val newChip = Chip(this.context)
//            newChip.text = text
//            newChip.isCloseIconVisible = true
//            newChip.isClickable = true
//            this.addView(newChip, this.childCount)
//
//            newChip.setOnCloseIconClickListener {
//                this.removeView(newChip)
//
//            }
//        }

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
