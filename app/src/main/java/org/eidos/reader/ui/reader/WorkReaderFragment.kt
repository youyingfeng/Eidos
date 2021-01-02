package org.eidos.reader.ui.reader

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import org.eidos.reader.R
import org.eidos.reader.databinding.FragmentWorkListBinding
import org.eidos.reader.databinding.FragmentWorkReaderBinding
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
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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


        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}