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

class WorkBlurbAdapter : RecyclerView.Adapter<WorkBlurbAdapter.ViewHolder>() {
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Timber.i("onBindViewHolder called")
        val item = data[position]
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkBlurbAdapter.ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
                    .fold(StringBuilder()) { acc, next -> acc.append(next) }
                    .toString()
            warnings.text = item.warnings
                    .fold(StringBuilder()) { acc, next -> acc.append(next) }
                    .toString()
            relationships.text = item.relationships
                    .fold(StringBuilder()) { acc, next -> acc.append(next) }
                    .toString()
            characters.text = item.characters
                    .fold(StringBuilder()) { acc, next -> acc.append(next) }
                    .toString()
            freeforms.text = item.freeforms
                    .fold(StringBuilder()) { acc, next -> acc.append(next) }
                    .toString()
            summary.text = HtmlCompat.fromHtml(item.summary, HtmlCompat.FROM_HTML_MODE_LEGACY)
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                // FIXME: consider doing viewbinding here and passing the binding object to the viewHolder instead
                // See: https://stackoverflow.com/questions/60491966/how-to-do-latest-jetpack-view-binding-in-adapter-bind-the-views
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.card_work_blurb, parent, false)
                return ViewHolder(view)
            }
        }
    }
}