package org.eidos.reader.ui.worklist

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.RecyclerView
import org.eidos.reader.R
import org.eidos.reader.databinding.FragmentWorkListBinding
import org.eidos.reader.remote.requests.WorkFilterRequest
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
        // TODO: Use the viewmodel
        val args = WorkListFragmentArgs.fromBundle(requireArguments())
        val tagName = args.tagName
        val workFilterRequest = WorkFilterRequest(tagName)

        viewModelFactory = WorkListViewModelFactory(workFilterRequest)
        viewModel = ViewModelProvider(this, viewModelFactory).get(WorkListViewModel::class.java)

        // inflates the root
        _binding = FragmentWorkListBinding.inflate(inflater, container, false)
        val view = binding.root

        // create the adapter
        val adapter = WorkBlurbAdapter { holderView, workBlurb ->
            holderView.findNavController()
                    .navigate(WorkListFragmentDirections
                            .actionWorkListFragmentToWorkReaderFragment(workBlurb.workURL))
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
                    Timber.i("Bottom of the list reached")
                    viewModel.getNextPage()
                } else if (recyclerView.canScrollVertically(-1) && dy < 0) {
                    // this should only hit when you scroll to the very top of the list
                    Timber.i("Top of the list reached")
                }
            }
        })

        Timber.i("WorkListFragment onCreateView completed")

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}