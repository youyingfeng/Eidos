package org.eidos.reader.ui.browse.worklist

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.recyclerview.widget.RecyclerView
import org.eidos.reader.R
import org.eidos.reader.databinding.FragmentWorkListBinding
import org.eidos.reader.remote.requests.WorkFilterRequest
import org.eidos.reader.ui.misc.utilities.Utilities.Companion.hideKeyboard
import org.eidos.reader.ui.misc.utilities.Utilities.Companion.setActivityTitle
import timber.log.Timber

class WorkListFragment : Fragment() {

    companion object {
        fun newInstance() = WorkListFragment()
    }

    private lateinit var viewModel: WorkListViewModel
    private lateinit var viewModelFactory: WorkListViewModelFactory

    private var _binding: FragmentWorkListBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // inflates the root
        _binding = FragmentWorkListBinding.inflate(inflater, container, false)
        val view = binding.root

        val args = WorkListFragmentArgs.fromBundle(requireArguments())
        val tagName = args.tagName
        val workFilterRequest = WorkFilterRequest(tagName)

        // Set up back button, title and filter button in the toolbar
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity).setupActionBarWithNavController(findNavController())
        setActivityTitle(tagName)
        setHasOptionsMenu(true)

        viewModelFactory = WorkListViewModelFactory(workFilterRequest)
        // scope this to the activity to enable sharing of data
        viewModel = ViewModelProvider(activity as AppCompatActivity, viewModelFactory).get(WorkListViewModel::class.java)
//        viewModel : WorkListViewModel by activityViewModels<>()



        // create the adapter
        val adapter = WorkBlurbAdapter { holderView, workBlurb ->
            holderView.findNavController()
                    .navigate(WorkListFragmentDirections
                            .actionWorkListFragmentToWorkReader(workBlurb.workURL))
        }
        binding.workListDisplay.adapter = adapter

        //get data into the adapter
        viewModel.workBlurbs.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.data = it
            }
        })

        // set scroll listener to load more data
        // possible but complicated soln: https://stackoverflow.com/questions/36127734/detect-when-recyclerview-reaches-the-bottom-most-position-while-scrolling/48514857#48514857
        // using the simple naive soln first,
        binding.workListDisplay.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (!recyclerView.canScrollVertically(1) && dy > 0) {
                    // this should hit when you have scrolled to the bottom of the list
                    // Timber.i("Bottom of the list reached")
                    viewModel.getNextPage()
                } else if (recyclerView.canScrollVertically(-1) && dy < 0) {
                    // this should only hit when you scroll to the very top of the list
                    // Timber.i("Top of the list reached")
                }
            }
        })

        Timber.i("WorkListFragment onCreateView completed")

        return view
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
                                .actionWorkListFragmentToWorkListFilterFragment())
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