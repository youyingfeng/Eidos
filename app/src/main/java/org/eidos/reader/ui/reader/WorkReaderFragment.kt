package org.eidos.reader.ui.reader

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.FOCUS_UP
import android.view.ViewGroup
import androidx.lifecycle.Observer
import org.eidos.reader.R
import org.eidos.reader.databinding.FragmentWorkListBinding
import org.eidos.reader.databinding.FragmentWorkReaderBinding
import org.eidos.reader.ui.utilities.Utilities.Companion.setActivityTitle
import org.eidos.reader.ui.worklist.WorkListViewModel

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

    val scrollToTop = { binding.mainScrollView.fullScroll(View.FOCUS_UP) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // get the workURL
        val args = WorkReaderFragmentArgs.fromBundle(requireArguments())
        val workURL = args.workURL

        // TODO: write factory to pass workURL to VM in constructor
        viewModelFactory = WorkReaderViewModelFactory(workURL)
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(WorkReaderViewModel::class.java)

        // Inflates the view
        _binding = FragmentWorkReaderBinding.inflate(inflater, container, false)
        val view = binding.root

        /* Setting observers */
        viewModel.currentChapterBody.observe(viewLifecycleOwner, Observer { chapterBody ->
            binding.workBody.text = chapterBody
        })

        viewModel.hasNextChapter.observe(viewLifecycleOwner, Observer { hasNextChapter ->
            binding.nextChapterButton.isEnabled = hasNextChapter
        })

        viewModel.hasPreviousChapter.observe(viewLifecycleOwner, Observer { hasPreviousChapter ->
            binding.previousChapterButton.isEnabled = hasPreviousChapter
        })

        // this code automatically scrolls the scrollview to the top upon laying out anything
        // FIXME: this section might cause a problem when comments are laid out in the future,
        // as the view might scroll to top even though the buttons have not been pressed.
        // solution obtained from: https://stackoverflow.com/questions/4119441/how-to-scroll-to-top-of-long-scrollview-layout
        binding.workBody.viewTreeObserver.addOnGlobalLayoutListener {
            scrollToTop
        }

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
            viewModel.loadNextChapter()
        }


        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // remove the global layout listener to prevent NPE
        binding.workBody.viewTreeObserver.removeOnGlobalLayoutListener { scrollToTop }
        _binding = null
    }
}