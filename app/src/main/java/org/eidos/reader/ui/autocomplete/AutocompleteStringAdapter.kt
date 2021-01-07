package org.eidos.reader.ui.autocomplete

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.eidos.reader.R
import org.eidos.reader.databinding.CardAutocompleteResultBinding
import timber.log.Timber

class AutocompleteStringAdapter : RecyclerView.Adapter<AutocompleteStringAdapter.AutocompleteStringViewHolder>() {
    var data = listOf<String>()
        set(value) {
            field = value
            notifyDataSetChanged()
            Timber.i("Autocomplete data set")
        }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: AutocompleteStringViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AutocompleteStringViewHolder {
        return AutocompleteStringViewHolder.from(parent)
    }

    class AutocompleteStringViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // store the binding here
        private val binding = CardAutocompleteResultBinding.bind(itemView)

        fun bind(item: String) {
            binding.resultText.text = item
        }

        companion object {
            fun from(parent: ViewGroup) : AutocompleteStringViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.card_work_blurb, parent, false)
                return AutocompleteStringViewHolder(view)
            }
        }
    }
}