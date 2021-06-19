package org.eidos.reader.ui.read.workinfo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.eidos.reader.R
import org.eidos.reader.databinding.FragmentWorkInfoBinding
import org.eidos.reader.model.WorkBlurb

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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().let {
            workBlurb = it.getParcelable<WorkBlurb>("workBlurb")!!
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkInfoBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // TODO: set view content here
        binding.test.text = workBlurb.title
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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