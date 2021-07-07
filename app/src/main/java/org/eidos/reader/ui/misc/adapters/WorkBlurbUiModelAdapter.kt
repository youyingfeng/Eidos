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
import org.eidos.reader.model.domain.WorkBlurb
import org.eidos.reader.model.ui.WorkBlurbUiModel
import org.eidos.reader.ui.misc.viewholders.SeparatorViewHolder
import timber.log.Timber
import kotlin.math.log
import kotlin.math.pow

/*
Adapter to translate WorkBlurb data to compact work blurb views.
 */

class WorkBlurbUiModelAdapter
    constructor(
        private val onClickAction: (View, WorkBlurb) -> Unit,
        private val onLongClickAction: (View, WorkBlurb) -> Unit,
    )
    : PagingDataAdapter<WorkBlurbUiModel, RecyclerView.ViewHolder>(workBlurbComparator)
{
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Timber.i("onBindViewHolder called")
        val uiModel = getItem(position)

        when (uiModel) {
            is WorkBlurbUiModel.WorkBlurbItem -> {
                (holder as WorkBlurbViewHolder).bind(uiModel.workBlurb)
                holder.itemView.setOnClickListener { view ->
                    onClickAction(view, uiModel.workBlurb)
                }
                holder.itemView.setOnLongClickListener { view ->
                    onLongClickAction(view, uiModel.workBlurb)
                    return@setOnLongClickListener true
                }
            }
            is WorkBlurbUiModel.SeparatorItem -> {
                (holder as SeparatorViewHolder).bind(uiModel.description)
                holder.itemView.setOnClickListener(null)
                holder.itemView.setOnClickListener(null)
            }
            null -> {
                holder.itemView.setOnClickListener(null)
                holder.itemView.setOnClickListener(null)
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is WorkBlurbUiModel.WorkBlurbItem -> R.layout.layout_work_blurb
            is WorkBlurbUiModel.SeparatorItem -> R.layout.separator_item
            null -> throw UnsupportedOperationException("Unknown view")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == R.layout.layout_work_blurb) {
            WorkBlurbViewHolder.from(parent)
        } else {
            SeparatorViewHolder.from(parent)
        }
    }

    companion object {
        private val workBlurbComparator = object : DiffUtil.ItemCallback<WorkBlurbUiModel>() {
            override fun areItemsTheSame(
                oldItem: WorkBlurbUiModel,
                newItem: WorkBlurbUiModel
            ): Boolean {
                return (oldItem is WorkBlurbUiModel.WorkBlurbItem
                        && newItem is WorkBlurbUiModel.WorkBlurbItem
                        && oldItem.workBlurb.workURL == newItem.workBlurb.workURL)
                        ||
                       (oldItem is WorkBlurbUiModel.SeparatorItem
                        && newItem is WorkBlurbUiModel.SeparatorItem
                        && oldItem.description == newItem.description)
            }

            override fun areContentsTheSame(
                oldItem: WorkBlurbUiModel,
                newItem: WorkBlurbUiModel
            ): Boolean {
                return oldItem == newItem
            }
        }
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
}

