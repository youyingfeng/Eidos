package org.eidos.reader.ui.readinglist

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.eidos.reader.R

class ReadingListFragment : Fragment() {

    companion object {
        fun newInstance() = ReadingListFragment()
    }

    private lateinit var viewModel: ReadingListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reading_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ReadingListViewModel::class.java)
        // TODO: Use the ViewModel
    }

}