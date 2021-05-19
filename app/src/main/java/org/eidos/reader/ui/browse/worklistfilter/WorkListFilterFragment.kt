package org.eidos.reader.ui.browse.worklistfilter

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.view.children
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.chip.Chip
import org.eidos.reader.databinding.FormWorkFilterBinding
import org.eidos.reader.ui.misc.autocomplete.AutocompleteStringAdapter
import org.eidos.reader.ui.misc.utilities.Utilities.Companion.hideKeyboard
import org.eidos.reader.ui.browse.worklist.WorkListViewModel
import timber.log.Timber

class WorkListFilterFragment : DialogFragment() {

    // by viewModels kotlin delegate is basically a lazy initialiser
    // FIXME: cannot create instance of VM
    private val viewModel : WorkListViewModel by activityViewModels()
    private val workListFilterViewModel : WorkListFilterViewModel by viewModels()
    /* The AutocompleteViewModel is meant to provide autocomplete livedata only */

    private var _binding: FormWorkFilterBinding? = null
    val binding : FormWorkFilterBinding
        get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FormWorkFilterBinding.inflate(LayoutInflater.from(context))

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideKeyboard()
        _binding = null
    }
}
