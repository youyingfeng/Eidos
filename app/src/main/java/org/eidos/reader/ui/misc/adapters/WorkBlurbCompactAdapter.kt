package org.eidos.reader.ui.misc.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.eidos.reader.R
import org.eidos.reader.databinding.LayoutWorkBlurbCompactBinding
import org.eidos.reader.model.domain.WorkBlurb
import timber.log.Timber
import kotlin.math.log
import kotlin.math.pow

/*
Adapter to translate WorkBlurb data to compact work blurb views.
 */

class WorkBlurbCompactAdapter
    constructor(
        private val onClickAction: (View, WorkBlurb) -> Unit,
        private val onLongClickAction: (View, WorkBlurb) -> Unit
    )
    : ListAdapter<WorkBlurb, WorkBlurbCompactAdapter.WorkBlurbCompactViewHolder>(WorkBlurbDiffCallback)
{
    override fun onBindViewHolder(holder: WorkBlurbCompactViewHolder, position: Int) {
        Timber.i("onBindViewHolder called")
        val workBlurb = getItem(position)
        holder.bind(workBlurb)
        holder.itemView.setOnClickListener { view ->
            onClickAction(view, workBlurb)
        }
        holder.itemView.setOnLongClickListener { view ->
            onLongClickAction(view, workBlurb)
            return@setOnLongClickListener true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkBlurbCompactViewHolder {
        return WorkBlurbCompactViewHolder.from(parent)
    }

    companion object WorkBlurbDiffCallback : DiffUtil.ItemCallback<WorkBlurb>() {
        override fun areItemsTheSame(oldItem: WorkBlurb, newItem: WorkBlurb): Boolean {
            return oldItem.workURL == newItem.workURL
        }

        override fun areContentsTheSame(oldItem: WorkBlurb, newItem: WorkBlurb): Boolean {
            return oldItem == newItem
        }
    }


    class WorkBlurbCompactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = LayoutWorkBlurbCompactBinding.bind(itemView)

        fun bind(item: WorkBlurb) {
            binding.workTitle.text = item.title
            binding.workAuthors.text = item.authors
                .fold(StringBuilder()) { acc, next -> acc.append(next).append(" · ") }
                .removeSuffix(" · ")
                .toString()

            binding.workRatingIcon.text = when(item.rating) {
                "Not Rated" -> ""
                "General Audiences" -> "G"
                "Teen And Up Audiences" -> "T"
                "Mature" -> "M"
                "Explicit" -> "E"
                else -> ""
            }

            binding.workRatingIconBackground.setCardBackgroundColor(when(item.rating) {
                "Not Rated" -> ContextCompat.getColor(
                    binding.workRatingIconBackground.context, R.color.white)
                "General Audiences" -> ContextCompat.getColor(
                    binding.workRatingIconBackground.context, R.color.rating_gen)
                "Teen And Up Audiences" -> ContextCompat.getColor(
                    binding.workRatingIconBackground.context, R.color.rating_teen)
                "Mature" -> ContextCompat.getColor(
                    binding.workRatingIconBackground.context, R.color.rating_mature)
                "Explicit" -> ContextCompat.getColor(
                    binding.workRatingIconBackground.context, R.color.rating_explicit)
                else -> ContextCompat.getColor(
                    binding.workRatingIconBackground.context, R.color.white)
            })

            binding.workFandoms.text = item.fandoms
                .fold(StringBuilder()) { acc, next -> acc.append(next).append(", ") }
                .removeSuffix(", ")
                .toString()
            binding.workWarnings.text = item.warnings
                .fold(StringBuilder()) { acc, next -> acc.append(next).append(", ") }
                .removeSuffix(", ")
                .toString()

            binding.workSummary.text = HtmlCompat.fromHtml(item.summary, HtmlCompat.FROM_HTML_MODE_LEGACY)

            binding.workLanguage.text = item.language
            binding.workWordCount.text = formatNumber(item.wordCount)
            binding.workChapters.text = "${item.chapterCount}/${if (item.maxChapters == 0) "?" else item.maxChapters.toString()}"

        }

        companion object {
            fun from(parent: ViewGroup): WorkBlurbCompactViewHolder {
                // FIXME: consider doing viewbinding here and passing the binding object to the viewHolder instead
                // See: https://stackoverflow.com/questions/60491966/how-to-do-latest-jetpack-view-binding-in-adapter-bind-the-views
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.layout_work_blurb_compact, parent, false)
                return WorkBlurbCompactViewHolder(view)
            }

            fun formatNumber(count: Int) : String {
                if (count < 1000) return count.toString()
                val exponent = log(count.toDouble(), 1000.toDouble())
                return String.format("%.1f %c", count / 1000.0.pow(exponent), "KMGTPE"[exponent.toInt() - 1])
            }
        }
    }
}
