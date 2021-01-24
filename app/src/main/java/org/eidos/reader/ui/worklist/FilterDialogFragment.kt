package org.eidos.reader.ui.worklist

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.eidos.reader.R

class FilterDialogFragment() : DialogFragment() {
    private val viewModel : WorkListViewModel by viewModels({requireParentFragment()})

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return requireActivity().let {
            val builder = MaterialAlertDialogBuilder(it)
            val inflater = requireActivity().layoutInflater

            builder.setView(inflater.inflate(R.layout.form_work_filter, null))
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
}