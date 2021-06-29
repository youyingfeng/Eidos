package org.eidos.reader.ui.misc.adapters

import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import org.eidos.reader.ui.misc.viewholders.WorkBlurbsLoadStateViewHolder

class WorkBlurbsLoadStateAdapter
    constructor(
        private val retryAction: () -> Unit
    )
    : LoadStateAdapter<WorkBlurbsLoadStateViewHolder>()
{
    override fun onBindViewHolder(holder: WorkBlurbsLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): WorkBlurbsLoadStateViewHolder {
        return WorkBlurbsLoadStateViewHolder.from(parent, retryAction)
    }
}
