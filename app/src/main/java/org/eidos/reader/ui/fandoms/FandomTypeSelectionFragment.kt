package org.eidos.reader.ui.fandoms

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import org.eidos.reader.R
import org.eidos.reader.databinding.FragmentFandomTypeSelectionBinding
import org.eidos.reader.databinding.FragmentWorkListBinding
import org.eidos.reader.remote.requests.WorkFilterRequest

class FandomTypeSelectionFragment : Fragment() {

    companion object {
        fun newInstance() = FandomTypeSelectionFragment()
    }

    private lateinit var viewModel: FandomTypeSelectionViewModel

    private var _binding: FragmentFandomTypeSelectionBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        viewModel = ViewModelProvider(this).get(FandomTypeSelectionViewModel::class.java)

        _binding = FragmentFandomTypeSelectionBinding.inflate(inflater, container, false)
        val view = binding.root

        // set click listeners
        binding.submitButton.setOnClickListener { view ->
            val tagName = binding.tagField.text.toString()
//            val workFilterRequest = WorkFilterRequest(tagName)

            view.findNavController()
                    .navigate(FandomTypeSelectionFragmentDirections
                            .actionFandomTypeSelectionFragmentToWorkListFragment(tagName))
        }


        return view
    }
}