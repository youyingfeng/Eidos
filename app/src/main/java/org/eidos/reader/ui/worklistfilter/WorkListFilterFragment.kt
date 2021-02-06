package org.eidos.reader.ui.worklistfilter

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.chip.Chip
import org.eidos.reader.databinding.FormWorkFilterBinding
import org.eidos.reader.ui.autocomplete.AutocompleteStringAdapter
import org.eidos.reader.ui.utilities.Utilities.Companion.hideKeyboard
import org.eidos.reader.ui.worklist.WorkListViewModel
import timber.log.Timber

class WorkListFilterFragment : DialogFragment() {

    // by viewModels kotlin delegate is basically a lazy initialiser
    private val viewModel : WorkListViewModel by viewModels({requireParentFragment()})
    private val workListFilterViewModel : WorkListFilterViewModel by viewModels()
    /* The AutocompleteViewModel is meant to provide autocomplete livedata only */

    private var _binding: FormWorkFilterBinding? = null
    val binding : FormWorkFilterBinding
        get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FormWorkFilterBinding.inflate(LayoutInflater.from(context))

        // set autocomplete adapters
        val includeTagsAutocompleteAdapter =
                AutocompleteStringAdapter { _: View, autocompleteResultString: String ->
                    binding.includeTagsFL.addChip(autocompleteResultString)
                    binding.includeTagsInputET.text.clear()
                }
        binding.autocompleteIncludeTagsRV.adapter = includeTagsAutocompleteAdapter

        val excludeTagsAutocompleteAdapter =
                AutocompleteStringAdapter { _: View, autocompleteResultString: String ->
                    binding.excludeTagsFL.addChip(autocompleteResultString)
                    binding.excludeTagsInputET.text.clear()
                }
        binding.autocompleteExcludeTagsRV.adapter = excludeTagsAutocompleteAdapter

        // FIXME: Below code does not work
        // change the enter button on keyboard to ADD
        // binding.includeTagsInputET.setImeActionLabel("ADD", EditorInfo.IME_ACTION_DONE)
        // binding.excludeTagsInputET.setImeActionLabel("ADD", EditorInfo.IME_ACTION_DONE)

        // add a chip when user presses enter
        binding.includeTagsInputET.setOnEditorActionListener { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    binding.includeTagsFL.addChip(binding.includeTagsInputET.text.toString())
                    binding.includeTagsInputET.text.clear()
                    return@setOnEditorActionListener true
                }
                else -> false
            }
        }

        binding.excludeTagsInputET.setOnEditorActionListener { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    binding.excludeTagsFL.addChip(binding.excludeTagsInputET.text.toString())
                    binding.excludeTagsInputET.text.clear()
                    return@setOnEditorActionListener true
                }
                else -> false
            }
        }

        // make respective RVs visible when the input field is selected, and hide them when focus is
        // lost
        binding.includeTagsInputET.setOnFocusChangeListener { _, hasFocus: Boolean ->
            Timber.i("Include tags focus changed")
            if (hasFocus) {
                binding.autocompleteIncludeTagsRV.visibility = View.VISIBLE
            } else {
                binding.autocompleteIncludeTagsRV.visibility = View.GONE
                workListFilterViewModel.clearAutocompleteResults()
            }
        }

        binding.excludeTagsInputET.setOnFocusChangeListener { _, hasFocus: Boolean ->
            Timber.i("Exclude tags focus changed")
            if (hasFocus) {
                binding.autocompleteExcludeTagsRV.visibility = View.VISIBLE
            } else {
                binding.autocompleteExcludeTagsRV.visibility = View.GONE
                workListFilterViewModel.clearAutocompleteResults()
            }
        }



        // fetch results when user stops typing
        binding.includeTagsInputET.afterTextChangedDelayed { inputString ->
            if (inputString.isNotBlank()) {
                workListFilterViewModel.fetchAutocompleteResults(inputString)
            } else {
                workListFilterViewModel.clearAutocompleteResults()
            }
        }

        binding.excludeTagsInputET.afterTextChangedDelayed { inputString ->
            if (inputString.isNotBlank()) {
                workListFilterViewModel.fetchAutocompleteResults(inputString)
            } else {
                workListFilterViewModel.clearAutocompleteResults()
            }
        }

        // pump the results to the respective adapters
        workListFilterViewModel.autocompleteResults.observe(viewLifecycleOwner, { resultsList ->
            includeTagsAutocompleteAdapter.data = resultsList
            excludeTagsAutocompleteAdapter.data = resultsList
        })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun FlexboxLayout.addChip(chipText: String) {
        // TODO: convert to textView and Drawables
        val chip = Chip(context)
        chip.text = chipText
        chip.isCloseIconVisible = true
        chip.isClickable = true
        chip.isCheckable = false
        chip.ellipsize = TextUtils.TruncateAt.END
        this.addView(chip as View, this.childCount - 1)
        chip.setOnCloseIconClickListener { this.removeView(chip as View) }
    }

    // Using extension functions as laid out in the answer by Emmanuel Gunther here:
    // https://stackoverflow.com/a/52944488
    fun TextView.afterTextChangedDelayed(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            var timer: CountDownTimer? = null

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun afterTextChanged(editable: Editable?) {
                timer?.cancel()
                timer = object : CountDownTimer(100, 100) {
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