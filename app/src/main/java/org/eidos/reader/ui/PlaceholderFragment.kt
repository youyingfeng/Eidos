package org.eidos.reader.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import org.eidos.reader.R
import org.eidos.reader.databinding.FragmentPlaceholderBinding
import org.eidos.reader.ui.fandoms.FandomTypeSelectionFragmentDirections

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PlaceholderFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlaceholderFragment : Fragment() {
    // TODO: Rename and change types of parameters

    private var _binding: FragmentPlaceholderBinding? = null
    private val binding: FragmentPlaceholderBinding
        get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPlaceholderBinding.inflate(layoutInflater, container, false)
        val view = binding.root

        // set click listeners
        binding.submitButton.setOnClickListener { view ->
            val tagName = binding.tagField.text.toString()

//            view.findNavController()
//                    .navigate(PlaceholderFragmentDirections
//                            .actionPlaceholderFragmentToWorkListFragment(tagName))
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}