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
import org.eidos.reader.SavedWorkBlurb
import org.eidos.reader.databinding.LayoutWorkBlurbBinding
import kotlin.math.log
import kotlin.math.pow

class SavedWorkBlurbAdapter
    constructor(
        private val onClickAction: (View, SavedWorkBlurb) -> Unit,
        private val onLongClickAction: (View, SavedWorkBlurb) -> Unit,
    )
    : PagingDataAdapter<SavedWorkBlurb, SavedWorkBlurbAdapter.SavedWorkBlurbViewHolder>(workBlurbComparator)
{

    override fun onBindViewHolder(holder: SavedWorkBlurbViewHolder, position: Int) {
        val savedWorkBlurb = getItem(position)
        if (savedWorkBlurb != null) {
            holder.bind(savedWorkBlurb)
            holder.itemView.setOnClickListener { view ->
                onClickAction(view, savedWorkBlurb)
            }

            holder.itemView.setOnLongClickListener { view ->
                onLongClickAction(view, savedWorkBlurb)
                return@setOnLongClickListener true
            }
        } else {
            holder.itemView.setOnClickListener(null)
            holder.itemView.setOnLongClickListener(null)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedWorkBlurbViewHolder {
        return SavedWorkBlurbViewHolder.from(parent)
    }

    companion object {
        private val workBlurbComparator = object : DiffUtil.ItemCallback<SavedWorkBlurb>() {
            override fun areItemsTheSame(
                oldItem: SavedWorkBlurb,
                newItem: SavedWorkBlurb
            ): Boolean {
                return oldItem.workURL == newItem.workURL
            }

            override fun areContentsTheSame(
                oldItem: SavedWorkBlurb,
                newItem: SavedWorkBlurb
            ): Boolean {
                return oldItem == newItem
            }
        }
    }


    class SavedWorkBlurbViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = LayoutWorkBlurbBinding.bind(itemView)

        init {
            binding.workHits.visibility = View.GONE
            binding.workKudos.visibility = View.GONE
            binding.workComments.visibility = View.GONE
            binding.workBookmarks.visibility = View.GONE
        }

        fun bind(item: SavedWorkBlurb) {
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
                    binding.workRatingIconBackground.context, R.color.rating_none)
                "General Audiences" -> ContextCompat.getColor(
                    binding.workRatingIconBackground.context, R.color.rating_gen)
                "Teen And Up Audiences" -> ContextCompat.getColor(
                    binding.workRatingIconBackground.context, R.color.rating_teen)
                "Mature" -> ContextCompat.getColor(
                    binding.workRatingIconBackground.context, R.color.rating_mature)
                "Explicit" -> ContextCompat.getColor(
                    binding.workRatingIconBackground.context, R.color.rating_explicit)
                else -> ContextCompat.getColor(
                    binding.workRatingIconBackground.context, R.color.rating_none)
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
                ?.fold(StringBuilder()) { acc, next -> acc.append(next).append(", ") }
                ?.removeSuffix(", ")
                ?.toString()
            binding.workCharacters.text = item.characters
                ?.fold(StringBuilder()) { acc, next -> acc.append(next).append(", ") }
                ?.removeSuffix(", ")
                ?.toString()
            binding.workFreeforms.text = item.freeforms
                ?.fold(StringBuilder()) { acc, next -> acc.append(next).append(", ") }
                ?.removeSuffix(", ")
                ?.toString()

            binding.workSummary.text = HtmlCompat.fromHtml(item.summary, HtmlCompat.FROM_HTML_MODE_LEGACY)

            binding.workLanguage.text = item.language
            binding.workWordCount.text = formatNumber(item.wordCount)
            binding.workChapters.text = "${item.chapterCount}/${if (item.maxChapters == 0) "?" else item.maxChapters.toString()}"
            binding.workDateUpdated.text = item.lastUpdatedDate
//            binding.workKudos.text = formatNumber(item.kudosCount)
//            binding.workComments.text = formatNumber(item.commentsCount)
//            binding.workBookmarks.text = formatNumber(item.bookmarksCount)
//            binding.workHits.text = formatNumber(item.hitCount)
        }

        companion object {
            fun from(parent: ViewGroup): SavedWorkBlurbViewHolder {
                // FIXME: consider doing viewbinding here and passing the binding object to the viewHolder instead
                // See: https://stackoverflow.com/questions/60491966/how-to-do-latest-jetpack-view-binding-in-adapter-bind-the-views
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_work_blurb, parent, false)
                return SavedWorkBlurbViewHolder(view)
            }

            fun formatNumber(count: Int) : String {
                if (count < 1000) return count.toString()
                val exponent = log(count.toDouble(), 1000.toDouble())
                return String.format("%.1f %c", count / 1000.0.pow(exponent), "KMGTPE"[exponent.toInt() - 1])
            }
        }
    }
}
