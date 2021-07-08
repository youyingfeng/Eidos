package org.eidos.reader.ui.read.workinfo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import org.eidos.reader.EidosApplication
import org.eidos.reader.WorkListDirections
import org.eidos.reader.container.AppContainer
import org.eidos.reader.databinding.FragmentWorkInfoBinding
import org.eidos.reader.model.domain.WorkBlurb

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [WorkInfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WorkInfoFragment : Fragment() {

    private var _binding: FragmentWorkInfoBinding? = null
    private val binding: FragmentWorkInfoBinding
        get() = _binding!!

    private lateinit var workBlurb: WorkBlurb
    private var isStoredInDatabase = false
    private lateinit var appContainer: AppContainer

    private val viewModel: WorkInfoViewModel by viewModels {
        WorkInfoViewModelFactory(workBlurb, isStoredInDatabase, appContainer.repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().let {
            workBlurb = it.getParcelable<WorkBlurb>("workBlurb")!!
            isStoredInDatabase = it.getBoolean("isStoredInDatabase")
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkInfoBinding.inflate(inflater, container, false)
        appContainer = (requireActivity().application as EidosApplication).appContainer

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity).setupActionBarWithNavController(findNavController())

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // TODO: set view content here

        // click listeners
        binding.saveWorkButton.setOnClickListener {
            viewModel.addWorkToLibrary(viewModel.workBlurb)
        }

        binding.saveReadingListButton.setOnClickListener {
            viewModel.addWorkToReadingList(viewModel.workBlurb)
        }

        binding.readButton.setOnClickListener {
            findNavController().navigate(
                WorkInfoFragmentDirections
                    .actionWorkInfoFragmentToWorkReaderFragment(
                        viewModel.workBlurb.workURL,
                        isStoredInDatabase
                    )
            )
        }

        // importing info
        binding.title.text = workBlurb.title
        binding.author.text = workBlurb.authors.joinToString()
        binding.giftees.text = workBlurb.giftees.joinToString()

        binding.rating.text = workBlurb.rating
        binding.warnings.text = workBlurb.warnings.joinToString()
        binding.categories.text = workBlurb.categories.joinToString()

        // add chips into the group
        workBlurb.fandoms.forEach { binding.fandomTags.addChipWithText(it) }
        workBlurb.relationships.forEach { binding.relationshipTags.addChipWithText(it) }
        workBlurb.characters.forEach { binding.characterTags.addChipWithText(it) }
        workBlurb.freeforms.forEach { binding.freeformTags.addChipWithText(it) }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun ChipGroup.addChipWithText(text: String) {
        val newChip = Chip(context)
        newChip.text = text
        newChip.isCloseIconVisible = false
        newChip.isClickable = true
        this.addView(newChip, this.childCount)

        newChip.setOnClickListener {
            findNavController().navigate(
                WorkListDirections.actionBrowseTag(text)
            )
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment WorkInfoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(workBlurb: WorkBlurb) =
            WorkInfoFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("workBlurb", workBlurb)
                }
            }
    }
}