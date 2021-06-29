package org.eidos.reader.ui.misc.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import org.eidos.reader.R
import org.eidos.reader.databinding.LayoutWorkBlurbBinding
import org.eidos.reader.model.WorkBlurb
import timber.log.Timber
import kotlin.math.log
import kotlin.math.pow

/*
Adapter to translate WorkBlurb data to compact work blurb views.
 */

class WorkBlurbAdapter
    constructor(
        private val onClickAction: (View, WorkBlurb) -> Unit,
        private val onLongClickAction: (View, WorkBlurb) -> Unit,
    )
    : PagingDataAdapter<WorkBlurb, WorkBlurbAdapter.WorkBlurbViewHolder>(workBlurbComparator)
{
//    var data = listOf<WorkBlurb>()
//        set(value) {
//            field = value
//            notifyDataSetChanged()
//            Timber.i("Adapter data set")
//            Timber.i(field.size.toString())
//        }

//    override fun getItemCount(): Int {
//        return data.size
//    }

    override fun onBindViewHolder(holder: WorkBlurbViewHolder, position: Int) {
        Timber.i("onBindViewHolder called")
        val workBlurb = getItem(position)
        if (workBlurb == null) {
            holder.itemView.setOnClickListener(null)
            holder.itemView.setOnLongClickListener(null)
        } else {
            holder.bind(workBlurb)
            holder.itemView.setOnClickListener { view ->
                onClickAction(view, workBlurb)
            }
            holder.itemView.setOnLongClickListener { view ->
                onLongClickAction(view, workBlurb)
                return@setOnLongClickListener true
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkBlurbViewHolder {
        return WorkBlurbViewHolder.from(parent)
    }

    class WorkBlurbViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = LayoutWorkBlurbBinding.bind(itemView)

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
            binding.workCategories.text = item.categories
                .fold(StringBuilder()) { acc, next -> acc.append(next).append(", ") }
                .removeSuffix(", ")
                .toString()
            binding.workRelationships.text = item.relationships
                .fold(StringBuilder()) { acc, next -> acc.append(next).append(", ") }
                .removeSuffix(", ")
                .toString()
            binding.workCharacters.text = item.characters
                .fold(StringBuilder()) { acc, next -> acc.append(next).append(", ") }
                .removeSuffix(", ")
                .toString()
            binding.workFreeforms.text = item.freeforms
                .fold(StringBuilder()) { acc, next -> acc.append(next).append(", ") }
                .removeSuffix(", ")
                .toString()

            binding.workSummary.text = HtmlCompat.fromHtml(item.summary, HtmlCompat.FROM_HTML_MODE_LEGACY)

            binding.workLanguage.text = item.language
            binding.workWordCount.text = formatNumber(item.wordCount)
            binding.workChapters.text = "${item.chapterCount}/${if (item.maxChapters == 0) "?" else item.maxChapters.toString()}"
            binding.workDateUpdated.text = item.lastUpdatedDate
            binding.workKudos.text = formatNumber(item.kudosCount)
            binding.workComments.text = formatNumber(item.commentsCount)
            binding.workBookmarks.text = formatNumber(item.bookmarksCount)
            binding.workHits.text = formatNumber(item.hitCount)
        }

        companion object {
            fun from(parent: ViewGroup): WorkBlurbViewHolder {
                // FIXME: consider doing viewbinding here and passing the binding object to the viewHolder instead
                // See: https://stackoverflow.com/questions/60491966/how-to-do-latest-jetpack-view-binding-in-adapter-bind-the-views
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.layout_work_blurb, parent, false)
                return WorkBlurbViewHolder(view)
            }

            fun formatNumber(count: Int) : String {
                if (count < 1000) return count.toString()
                val exponent = log(count.toDouble(), 1000.toDouble())
                return String.format("%.1f %c", count / 1000.0.pow(exponent), "KMGTPE"[exponent.toInt() - 1])
            }
        }
    }

    companion object {
        private val workBlurbComparator = object : DiffUtil.ItemCallback<WorkBlurb>() {
            override fun areItemsTheSame(oldItem: WorkBlurb, newItem: WorkBlurb): Boolean {
                return oldItem.workURL == newItem.workURL
            }

            override fun areContentsTheSame(oldItem: WorkBlurb, newItem: WorkBlurb): Boolean {
                return oldItem == newItem
            }
        }
    }
}

