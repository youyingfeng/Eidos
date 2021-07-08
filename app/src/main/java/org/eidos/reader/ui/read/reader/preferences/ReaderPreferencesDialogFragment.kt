package org.eidos.reader.ui.read.reader.preferences

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.slider.Slider
import org.eidos.reader.R
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

        // FIXME: enforce at least one selected
        if (viewModel.uiPreferences.value?.useNightMode == true) {
            binding.themeToggle.check(R.id.darkModeButton)
        } else {
            // false or null - light mode by default
            binding.themeToggle.check(R.id.lightModeButton)
        }

        binding.themeToggle.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                if (checkedId == R.id.lightModeButton) {
                    viewModel.setNightMode(false)
                } else if (checkedId == R.id.darkModeButton) {
                    viewModel.setNightMode(true)
                }
            }
        }

        binding.fontSizeSlider.value = viewModel.uiPreferences.value?.readerTextSize ?: 16F  // set initial value

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