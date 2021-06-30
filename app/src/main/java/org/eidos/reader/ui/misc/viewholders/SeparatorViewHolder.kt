package org.eidos.reader.ui.misc.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.eidos.reader.R
import org.eidos.reader.databinding.SeparatorItemBinding

class SeparatorViewHolder(private val binding: SeparatorItemBinding)
    : RecyclerView.ViewHolder(binding.root)
{
    fun bind(separatorText: String) {
        binding.description.text = separatorText
    }

    companion object {
        fun from(parent: ViewGroup): SeparatorViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.separator_item, parent, false)
            val binding = SeparatorItemBinding.bind(view)
            return SeparatorViewHolder(binding)
        }
    }
}
