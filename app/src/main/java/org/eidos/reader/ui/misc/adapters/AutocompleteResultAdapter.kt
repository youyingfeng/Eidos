package org.eidos.reader.ui.misc.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.eidos.reader.R
import org.eidos.reader.databinding.ListItemBinding
import timber.log.Timber

class AutocompleteResultAdapter(private val onClickAction: (View, String) -> Unit)
    : RecyclerView.Adapter<AutocompleteResultAdapter.AutocompleteResultViewHolder>()
{
    var data = listOf<String>()
        set(value) {
            field = value
            notifyDataSetChanged()
            Timber.i("Autocomplete data set")
        }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: AutocompleteResultViewHolder, position: Int) {
        val autocompleteResult = data[position]
        holder.bind(autocompleteResult)
        holder.itemView.setOnClickListener { view ->
            onClickAction(view, autocompleteResult)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AutocompleteResultViewHolder {
        return AutocompleteResultViewHolder.from(parent)
    }

    class AutocompleteResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ListItemBinding.bind(itemView)

        fun bind(item: String) {
            binding.description.text = item
        }

        companion object {
            fun from(parent: ViewGroup) : AutocompleteResultViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item, parent, false)
                return AutocompleteResultViewHolder(view)
            }
        }
    }
}
