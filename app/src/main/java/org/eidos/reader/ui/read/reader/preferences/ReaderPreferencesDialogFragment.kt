package org.eidos.reader.ui.read.reader.preferences

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.slider.Slider
import org.eidos.reader.databinding.DialogReaderOptionsBinding
import org.eidos.reader.ui.read.reader.WorkReaderViewModel

class ReaderPreferencesDialogFragment : BottomSheetDialogFragment()
{
    private var _binding: DialogReaderOptionsBinding? = null
    val binding: DialogReaderOptionsBinding
        get() = _binding!!

    private val viewModel: WorkReaderViewModel by viewModels({ requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogReaderOptionsBinding.inflate(inflater, container, false)

        binding.fontSizeSlider.value = viewModel.textSize.value ?: 16F  // set initial value

        binding.fontSizeSlider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {
                // nothing needs to be done for now
            }

            override fun onStopTrackingTouch(slider: Slider) {
                viewModel.updateTextSize(slider.value)
            }

        })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}