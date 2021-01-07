package org.eidos.reader.ui.fandoms

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import org.eidos.reader.R
import org.eidos.reader.databinding.FragmentFandomTypeSelectionBinding
import org.eidos.reader.databinding.FragmentWorkListBinding
import org.eidos.reader.remote.requests.WorkFilterRequest
import org.eidos.reader.ui.autocomplete.AutocompleteStringAdapter

class FandomTypeSelectionFragment : Fragment() {

    companion object {
        fun newInstance() = FandomTypeSelectionFragment()
    }

    private lateinit var viewModel: FandomTypeSelectionViewModel

    private var _binding: FragmentFandomTypeSelectionBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        viewModel = ViewModelProvider(this).get(FandomTypeSelectionViewModel::class.java)

        _binding = FragmentFandomTypeSelectionBinding.inflate(inflater, container, false)
        val view = binding.root

        // initialise the adapter with the onclicklistener
        // onclicklistener navigates to the next fragment
        val adapter = AutocompleteStringAdapter { holderView: View, autocompleteResultString: String ->
            holderView.findNavController()
                .navigate(FandomTypeSelectionFragmentDirections
                    .actionFandomTypeSelectionFragmentToWorkListFragment(autocompleteResultString))
        }
        binding.autocompleteResultsDisplay.adapter = adapter

        // get data into adapter
        viewModel.autocompleteResults.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.data = it
            }
        })

        // TODO: set listeners to listen for when user stops typing, and when user clicks on a
        // suggestion

        binding.searchInputField.afterTextChangedDelayed {
            viewModel.fetchAutocompleteResults(it)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Using extension functions as laid out in the answer by Emmanuel Gunther here:
    // https://stackoverflow.com/questions/35224459/how-to-detect-if-users-stop-typing-in-edittext-android
    fun TextView.afterTextChangedDelayed(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            var timer: CountDownTimer? = null

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                timer?.cancel()
                timer = object : CountDownTimer(500, 500) {
                    override fun onTick(millisUntilFinished: Long) {}
                    override fun onFinish() {
                        afterTextChanged.invoke(editable.toString())
                    }
                }.start()
            }
        })
    }
}