package org.eidos.reader.ui.worklist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import org.eidos.reader.R
import org.eidos.reader.databinding.CardWorkBlurbBinding
import org.eidos.reader.model.WorkBlurb
import timber.log.Timber

class WorkBlurbAdapter(private val onClickAction: (View, WorkBlurb) -> Unit) : RecyclerView.Adapter<WorkBlurbAdapter.WorkBlurbViewHolder>() {
    var data = listOf<WorkBlurb>()
        set(value) {
            field = value
            notifyDataSetChanged()
            Timber.i("Adapter data set")
            Timber.i(field.size.toString())
        }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: WorkBlurbViewHolder, position: Int) {
        Timber.i("onBindViewHolder called")
        val workBlurb = data[position]
        holder.bind(workBlurb)
        holder.itemView.setOnClickListener { view ->
            onClickAction(view, workBlurb)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkBlurbViewHolder {
        return WorkBlurbViewHolder.from(parent)
    }

    class WorkBlurbViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // binding solution obtained from stackoverflow: https://stackoverflow.com/questions/60491966/how-to-do-latest-jetpack-view-binding-in-adapter-bind-the-views
        private val binding = CardWorkBlurbBinding.bind(itemView)

        val title = binding.workTitle
        val authors = binding.workAuthors
        val warnings = binding.workWarnings
        val relationships = binding.workRelationships
        val characters = binding.workCharacters
        val freeforms = binding.workFreeforms
        val summary = binding.workSummary

        fun bind(item: WorkBlurb) {
            Timber.i("WorkBlurb bound!")
            title.text = item.title
            authors.text = item.authors
                    .fold(StringBuilder()) { acc, next -> acc.append(next).append(", ") }
                    .removeSuffix(", ")
                    .toString()
            warnings.text = item.warnings
                    .fold(StringBuilder()) { acc, next -> acc.append(next).append(", ") }
                    .removeSuffix(", ")
                    .toString()
            relationships.text = item.relationships
                    .fold(StringBuilder()) { acc, next -> acc.append(next).append(", ") }
                    .removeSuffix(", ")
                    .toString()
            characters.text = item.characters
                    .fold(StringBuilder()) { acc, next -> acc.append(next).append(", ") }
                    .removeSuffix(", ")
                    .toString()
            freeforms.text = item.freeforms
                    .fold(StringBuilder()) { acc, next -> acc.append(next).append(", ") }
                    .removeSuffix(", ")
                    .toString()
            summary.text = HtmlCompat.fromHtml(item.summary, HtmlCompat.FROM_HTML_MODE_LEGACY)
        }

        companion object {
            fun from(parent: ViewGroup): WorkBlurbViewHolder {
                // FIXME: consider doing viewbinding here and passing the binding object to the viewHolder instead
                // See: https://stackoverflow.com/questions/60491966/how-to-do-latest-jetpack-view-binding-in-adapter-bind-the-views
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.card_work_blurb, parent, false)
                return WorkBlurbViewHolder(view)
            }
        }
    }
}