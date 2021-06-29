package org.eidos.reader.ui.misc.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import org.eidos.reader.R
import org.eidos.reader.databinding.PagingLoadStateFooterBinding

class WorkBlurbsLoadStateViewHolder
    constructor(
        private val binding: PagingLoadStateFooterBinding,
        retryAction: () -> Unit
    )
    : RecyclerView.ViewHolder(binding.root)
{
    init {
        binding.retryButton.setOnClickListener { retryAction.invoke() }
    }

    fun bind(loadState: LoadState) {
        if (loadState is LoadState.Error) {
            binding.errorMsg.text = loadState.error.localizedMessage
        }
        binding.progressBar.isVisible = loadState is LoadState.Loading
        binding.retryButton.isVisible = loadState is LoadState.Error
        binding.errorMsg.isVisible = loadState is LoadState.Error
    }

    companion object {
        fun from(parent: ViewGroup, retryAction: () -> Unit): WorkBlurbsLoadStateViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.paging_load_state_footer, parent, false)
            val binding = PagingLoadStateFooterBinding.bind(view)
            return WorkBlurbsLoadStateViewHolder(binding, retryAction)
        }
    }
}
