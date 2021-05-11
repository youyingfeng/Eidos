package org.eidos.reader.ui.read.reader

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.lifecycle.Observer
import org.eidos.reader.WorkReaderArgs
import org.eidos.reader.databinding.FragmentWorkReaderBinding
import org.eidos.reader.ui.misc.utilities.Utilities.Companion.setActivityTitle
import timber.log.Timber

/*
TODO: Implement a collapsible toolbar as laid out in the design docs
TODO: Double-tap to bring up toolbar. Do not implement triple tap/single tap as accidents happen.
 */

class WorkReaderFragment : Fragment() {

    companion object {
        fun newInstance() = WorkReaderFragment()
    }

    // TODO: Fragment takes in an argument: the url of the work
    // Fragment then calls the rest of the content

    private lateinit var viewModel: WorkReaderViewModel
    private lateinit var viewModelFactory: WorkReaderViewModelFactory

    private var _binding: FragmentWorkReaderBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // get the workURL
        val args = WorkReaderArgs.fromBundle(requireArguments())
        val workURL = args.workURL

        // TODO: write factory to pass workURL to VM in constructor
        viewModelFactory = WorkReaderViewModelFactory(workURL)
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(WorkReaderViewModel::class.java)

        // Inflates the view
        _binding = FragmentWorkReaderBinding.inflate(inflater, container, false)
        val view = binding.root

        /* Setting observers */
        viewModel.currentChapterBody.observe(viewLifecycleOwner, { chapterBody ->
            binding.workBody.text = chapterBody

            // this code automatically scrolls the scrollview to the top upon laying out anything
            // FIXED: this section might cause a problem when comments are laid out in the future,
            // as the view might scroll to top even though the buttons have not been pressed.
            // solution obtained from: https://stackoverflow.com/questions/4119441/how-to-scroll-to-top-of-long-scrollview-layout
            binding.mainScrollView.viewTreeObserver.addOnGlobalLayoutListener(object:
                ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        binding.mainScrollView.fullScroll(View.FOCUS_UP)
                        Timber.i("OnGlobalLayout called!")

                        // remove the global layout listener to prevent NPE
                        // if the listener is not cleared, then a crash will occur.
                        binding.mainScrollView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        Timber.i("OnGlobalLayoutListener removed!")
                    }
                }
            )
            Timber.i("OnGlobalLayoutListener added")
        })

        viewModel.hasNextChapter.observe(viewLifecycleOwner, { hasNextChapter ->
            binding.nextChapterButton.isEnabled = hasNextChapter
        })

        viewModel.hasPreviousChapter.observe(viewLifecycleOwner, { hasPreviousChapter ->
            binding.previousChapterButton.isEnabled = hasPreviousChapter
        })

        viewModel.currentChapterIndicatorString.observe(viewLifecycleOwner,
                { currentChapterIndicatorString ->
            binding.currentChapterIndicator.text = currentChapterIndicatorString
        })

        // this code sets the title automatically
        // FIXME: race conditions may happen bc the chapter number may not be updated before the title
        // Temp solution: observe both title and chapter number. This works, but is hacky.
        // Actually, chapter number will defo be updated.
        viewModel.currentChapterTitle.observe(viewLifecycleOwner, Observer { title ->
            when(title.isBlank()) {
                // FIXME: below code violates separation of concerns as calculation should happen in viewmodel instead
                true -> setActivityTitle("Chapter ${viewModel.currentChapterIndex + 1}")
                false -> setActivityTitle("Chapter ${viewModel.currentChapterIndex + 1}: $title")
            }
        })

        /* Set onClickListeners*/
        binding.nextChapterButton.setOnClickListener {
            // do i need to block further calls? will this block on UI thread? if it blocks should
            // make this async and block further calls
            viewModel.loadNextChapter()
        }

        binding.previousChapterButton.setOnClickListener {
            // same concerns as above.
            viewModel.loadPreviousChapter()
        }

        binding.currentChapterIndicator.setOnClickListener {
            openChapterSelectionDialog()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // methods for handling chapter selection dialog
    fun openChapterSelectionDialog() {
        val dialogFragment = ChapterSelectionDialogFragment.newInstance(
                viewModel.chapterTitles.value?.toTypedArray()!!
        )
        dialogFragment.show(childFragmentManager, "selectChapter")
        // dialog will auto close when any button is tapped
    }
}