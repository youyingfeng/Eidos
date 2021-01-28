package org.eidos.reader.ui.worklist

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.eidos.reader.R
import org.eidos.reader.databinding.FormWorkFilterBinding

class FilterDialogFragment() : DialogFragment() {
    // by viewModels kotlin delegate is basically a lazy initialiser
    private val viewModel : WorkListViewModel by viewModels({requireParentFragment()})
    private val autocompleteViewModel : AutocompleteViewModel by viewModels()
    /* The AutocompleteViewModel is meant to provide autocomplete livedata only */

    private var _binding: FormWorkFilterBinding? = null
    val binding : FormWorkFilterBinding
        get() = _binding!!


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // TODO: initialise the autocompleteViewModel

        return requireActivity().let {
            val builder = MaterialAlertDialogBuilder(it)

            _binding = FormWorkFilterBinding.inflate(LayoutInflater.from(context))

            // set up adapters
            // TODO: modify the behaviour of clicking on autocomplete item
            // this is an anonymous adapter, not stored in the vm.
            val autocompleteAdapter = ArrayAdapter<String>(requireContext(),
                android.R.layout.simple_list_item_1,
                autocompleteViewModel.autocompleteResults.value as Array<out String>)
                .also { arrayAdapter ->
                    arrayAdapter.setNotifyOnChange(true)    // should auto notify, but might not work.
                    // but includeTagInput.adapter is val and cannot be reassigned? weird.
                }

            // set up listeners
            /* this listener updates the adapter dataset every time the livedata changes */
            autocompleteViewModel.autocompleteResults.observe(viewLifecycleOwner, { results ->
                // this should modify all fields since they are the same adapter
                autocompleteAdapter.clear()
                autocompleteAdapter.addAll(*results)
            })

            // define behaviour when autocomplete result is clicked



            builder.setView(binding.root)
                    .setPositiveButton("Filter",
                            DialogInterface.OnClickListener { dialog, id ->
                                // TODO: submit data to the viewmodel
                            }
                    )
                    .setNegativeButton("Cancel",
                            DialogInterface.OnClickListener { dialog, id ->
                                // do nothing
                            }
                    )
            builder.create()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}