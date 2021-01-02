package org.eidos.reader.ui.worklist

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import org.eidos.reader.R
import org.eidos.reader.databinding.FragmentWorkListBinding

class WorkListFragment : Fragment() {
    companion object {
        fun newInstance() = WorkListFragment()
    }

    private lateinit var viewModel: WorkListViewModel

    private var _binding: FragmentWorkListBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // TODO: Use the viewmodel
        viewModel = ViewModelProvider(this).get(WorkListViewModel::class.java)
        _binding = FragmentWorkListBinding.inflate(inflater, container, false)
        val view = binding.root

        /* Set up onClickListeners */
        binding.button.setOnClickListener { view ->
            var workURL = binding.inputWorkURL.text.toString()
            print(workURL)

            if (workURL == "/works/") {
                workURL = "/works/25245133"
            }

            view.findNavController()
                .navigate(WorkListFragmentDirections
                    .actionWorkListFragmentToWorkReaderFragment(workURL))
        }


        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}