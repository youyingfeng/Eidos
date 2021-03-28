package org.eidos.reader.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import org.eidos.reader.MainActivity
import org.eidos.reader.R
import org.eidos.reader.databinding.FragmentFandomTypeSelectionBinding
import org.eidos.reader.databinding.FragmentHomeBinding
import org.eidos.reader.ui.fandoms.FandomTypeSelectionViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()

    private var _binding: FragmentHomeBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Initialise the toolbar
        val navController = findNavController()
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        binding.toolbar.setupWithNavController(navController, (activity as MainActivity).drawerLayout)
        // Note: https://stackoverflow.com/a/46070579 fixes title not appearing on toolbar
        // TODO: create search interface for back container

        // TODO: write spannable strings to style navigation view - else replace with LL menu

        // TODO: write placeholder instructions for front container

        // returns the root view of the binding
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}