package org.eidos.reader.ui.browse.worklist

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.paging.LoadState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.eidos.reader.EidosApplication
import org.eidos.reader.R
import org.eidos.reader.WorkDirections
import org.eidos.reader.container.AppContainer
import org.eidos.reader.databinding.FragmentWorkListBinding
import org.eidos.reader.network.Network
import org.eidos.reader.remote.AO3
import org.eidos.reader.remote.choices.WorkFilterChoices
import org.eidos.reader.remote.requests.WorkFilterRequest
import org.eidos.reader.ui.misc.adapters.WorkBlurbUiModelAdapter
import org.eidos.reader.ui.misc.adapters.WorkBlurbsLoadStateAdapter
import org.eidos.reader.ui.misc.utilities.Utilities.Companion.hideKeyboard
import org.eidos.reader.ui.misc.utilities.Utilities.Companion.setActivityTitle
import timber.log.Timber

class WorkListFragment : Fragment() {

    companion object {
        fun newInstance() = WorkListFragment()

        private val options = arrayOf("Download work to Library", "Add work to Reading List")
    }

    private val viewModel: WorkListViewModel by viewModels {
        WorkListViewModelFactory(appContainer.repository, workFilterRequest)
    }

    private var _binding: FragmentWorkListBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var appContainer: AppContainer
    private lateinit var workFilterRequest: WorkFilterRequest

    private var currentJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appContainer = (requireActivity().application as EidosApplication).appContainer

        val args = WorkListFragmentArgs.fromBundle(requireArguments())
        workFilterRequest = WorkFilterRequest(args.tagName)

        setFragmentResultListener("updatedFilterChoices") { requestKey, bundle ->
            bundle.getParcelable<WorkFilterChoices>("workFilterChoices")?.let { updatedChoices ->
                // TODO: update the VM field and listen to the new VM field again here
                viewModel.updateFilterChoices(updatedChoices)
                Timber.i("result received")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // inflates the root
        _binding = FragmentWorkListBinding.inflate(inflater, container, false)

        // Set up back button, title and filter button in the toolbar
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity).setupActionBarWithNavController(findNavController())
        setHasOptionsMenu(true)

        // Update tag title and work count
        viewModel.metadata.observe(viewLifecycleOwner) { metadata ->
            binding.tagNameTextView.text = metadata.tagName
            setActivityTitle(metadata.tagName)
            binding.workCountTextView.text = "Listing ${metadata.workCount} works"
        }

        // handle errors
        viewModel.exception.observe(viewLifecycleOwner) {
            when (it) {
                is Network.ServerException -> {
                    Snackbar.make(binding.coordinator, "Server error", Snackbar.LENGTH_INDEFINITE)
                        .show()
                }
                is AO3.TagNotFilterableException -> {
                    Snackbar.make(binding.coordinator, "Tag cannot be filtered on", Snackbar.LENGTH_INDEFINITE)
                        .show()
                    binding.workListDisplay.clearOnScrollListeners()
                }
            }
        }

        val adapter = WorkBlurbUiModelAdapter(
            {
                holderView, workBlurb ->
                    holderView.findNavController()
                        .navigate(WorkDirections.actionShowWorkInfo(workBlurb, false))
            },
            { holderView, workBlurb ->
                MaterialAlertDialogBuilder(holderView.context)
                    .setTitle(workBlurb.title)
                    .setItems(options) { dialog, which ->
                        when(which) {
                            0 -> viewModel.addWorkToLibrary(workBlurb)
                            1 -> viewModel.addWorkToReadingList(workBlurb)
                        }
                    }
                    .show()
            }
        )
        binding.workListDisplay.adapter = adapter.withLoadStateHeaderAndFooter(
            header = WorkBlurbsLoadStateAdapter { adapter.retry() },
            footer = WorkBlurbsLoadStateAdapter { adapter.retry() }
        )
        adapter.addLoadStateListener { loadState ->
            val isListEmpty = loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0

            val isRefreshSuccessful = loadState.source.refresh is LoadState.NotLoading
            val isRefreshing = loadState.source.refresh is LoadState.Loading
            val isRefreshUnsuccessful = loadState.source.refresh is LoadState.Error
            // Only show the list if refresh succeeds and not empty.
            binding.workListDisplay.isVisible = isRefreshSuccessful && !isListEmpty
            // show the loading state otherwise
            binding.emptyListLoadStateDisplay.isVisible = !isRefreshSuccessful || isListEmpty
            // Show loading spinner during initial load or refresh.
            binding.progressBar.isVisible = isRefreshing
            // Show the retry state if initial load or refresh fails.
            binding.retryButton.isVisible = isRefreshUnsuccessful
            // show an empty list if list turns out to be empty
            binding.emptyList.isVisible = isListEmpty
                    && loadState.source.prepend.endOfPaginationReached
                    && loadState.source.append.endOfPaginationReached

            // Toast on any error, regardless of whether it came from RemoteMediator or PagingSource
            val errorState = loadState.source.append as? LoadState.Error
                ?: loadState.source.prepend as? LoadState.Error
                ?: loadState.append as? LoadState.Error
                ?: loadState.prepend as? LoadState.Error
            errorState?.let {
                Toast.makeText(
                    context,
                    "\uD83D\uDE28 Wooops ${it.error}",
                    Toast.LENGTH_LONG
                ).show()
            }

        }
        binding.retryButton.setOnClickListener { adapter.retry() }

        // listens for changes to the flow
        viewModel.workBlurbFlow.observe(viewLifecycleOwner) { pagingDataFlow ->
            currentJob?.cancel()
            currentJob = viewLifecycleOwner.lifecycleScope.launch {
                pagingDataFlow.collectLatest {
                    adapter.submitData(it)
                }
            }
        }

        lifecycleScope.launch {
            adapter.loadStateFlow
                .distinctUntilChangedBy { it.refresh }
                .filter { it.refresh is LoadState.NotLoading }
                .collect { binding.workListDisplay.scrollToPosition(0) }
        }
        

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // only include super.onCreateOptionsMenu(menu, inflater) if you want to include parent menu
        inflater.inflate(R.menu.work_list_options_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.filterWorksAction -> {
                hideKeyboard()
                findNavController()
                        .navigate(WorkListFragmentDirections
                                .actionWorkListFragmentToWorkListFilterFragment(
                                    viewModel.getWorkFilterChoices()
                                )
                        )
            }
        }

        // return true to consume the event here, false to forward the event elsewhere
        // calling super method allows the parent to handle the back button press
        // as mentioned in this answer: https://stackoverflow.com/a/48659833
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideKeyboard()
        _binding = null
        setHasOptionsMenu(false)
    }
}
