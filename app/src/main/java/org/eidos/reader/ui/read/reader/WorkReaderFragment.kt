package org.eidos.reader.ui.read.reader

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import org.eidos.reader.EidosApplication
import org.eidos.reader.R
import org.eidos.reader.WorkReaderArgs
import org.eidos.reader.container.AppContainer
import org.eidos.reader.databinding.FragmentWorkReaderBinding
import org.eidos.reader.ui.misc.utilities.URLImageGetter
import org.eidos.reader.ui.misc.utilities.Utilities.Companion.setActivityTitle
import org.eidos.reader.ui.read.reader.preferences.ReaderPreferencesDialogFragment
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

    private lateinit var appContainer: AppContainer

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        appContainer = (requireActivity().application as EidosApplication).appContainer
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflates the view
        _binding = FragmentWorkReaderBinding.inflate(inflater, container, false)
        appContainer = (requireActivity().application as EidosApplication).appContainer

        // Set up the ViewModel
        val args = WorkReaderArgs.fromBundle(requireArguments())
        val urlImageGetter = URLImageGetter(binding.workBody, appContainer.imageLoader)
        viewModelFactory = WorkReaderViewModelFactory(
            args.workURL,
            args.fetchFromDatabase,
            appContainer.repository,
            urlImageGetter
        )
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(WorkReaderViewModel::class.java)

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity).setupActionBarWithNavController(findNavController())
        setHasOptionsMenu(true)

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

        viewModel.textSize.observe(viewLifecycleOwner) {
            binding.workBody.textSize = it
        }

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

        /* COMMENTS */
        val adapter = CommentAdapter()
        binding.commentsDisplay.adapter = adapter

        viewModel.comments.observe(viewLifecycleOwner, {
            it?.let {
                adapter.data = it
                print(it)
                Timber.i("comments loaded")
            }
        })

//        binding.commentsSwipeRefresh.setOnRefreshListener {
//            Timber.i("Swipe refresh initiated")
//            binding.commentsSwipeRefresh.isRefreshing = true
//            viewModel.getNextCommentsPage()
//            binding.commentsSwipeRefresh.isRefreshing = false
//        }

        // load comments every chapter
        viewModel.currentChapterBody.observe(viewLifecycleOwner, {
            viewModel.getNextCommentsPage()
        })


        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_work_reader, menu)
//        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.saveWork -> {
                Timber.i("Save work pressed and received!")
                viewModel.saveWorkToDatabase()
                return true
            }
            R.id.showReaderPreferences -> {
                openReaderPreferencesDialog()
                return true
            }
        }

        return false
    }

    // methods for handling chapter selection dialog
    fun openChapterSelectionDialog() {
        val dialogFragment = ChapterSelectionDialogFragment.newInstance(
                viewModel.chapterTitles.value?.toTypedArray()!!
        )
        dialogFragment.show(childFragmentManager, "selectChapter")
        // dialog will auto close when any button is tapped
    }

    fun openReaderPreferencesDialog() {
        val dialogFragment = ReaderPreferencesDialogFragment()
        dialogFragment.show(childFragmentManager, "setReaderPreferences")
    }
}