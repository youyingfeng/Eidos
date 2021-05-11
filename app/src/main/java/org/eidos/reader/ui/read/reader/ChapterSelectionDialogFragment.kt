package org.eidos.reader.ui.read.reader

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import timber.log.Timber


class ChapterSelectionDialogFragment() : DialogFragment() {
    // FIXME: Do not overload constructor else fragment recreation will be problematic.
    // see https://stackoverflow.com/questions/9245408/best-practice-for-instantiating-a-new-android-fragment

    companion object {
        fun newInstance(chapterTitles: Array<String>) : DialogFragment {
            val newDialogFragment = ChapterSelectionDialogFragment()

            val args = Bundle()
            args.putStringArray("chapterTitles", chapterTitles)
            newDialogFragment.arguments = args

            return newDialogFragment
        }
    }

    private val viewModel : WorkReaderViewModel by viewModels({requireParentFragment()})

    private lateinit var chapterTitles : Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chapterTitles = requireArguments().getStringArray("chapterTitles") as Array<String>
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // use requireActivity instead of activity?. bc IDE will complain otherwise
        // see https://stackoverflow.com/questions/56168026/how-to-create-alertdialog-in-androidx-appcompat
        return requireActivity().let {
            val builder = MaterialAlertDialogBuilder(it)
            builder.setTitle("Jump To Chapter")
                    .setItems(chapterTitles,
                        DialogInterface.OnClickListener { dialog, id ->
                            Timber.i("Menu item #$id selected!")
                            viewModel.loadChapterAtIndex(id)
                        })
                    .setNegativeButton("Back",
                        DialogInterface.OnClickListener { dialog, id ->
                            // do nothing
                        })
            builder.show()
        }
    }
}