package org.eidos.reader.ui.read.reader

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.eidos.reader.R
import org.eidos.reader.databinding.LayoutCommentBinding
import org.eidos.reader.model.Comment
import timber.log.Timber

class CommentAdapter
    : RecyclerView.Adapter<CommentViewHolder>()
{
    var data = listOf<Comment>()
        set(value) {
            field = value
            notifyDataSetChanged()
            Timber.i("Adapter data set")
            Timber.i(field.size.toString())
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = data[position]
        holder.bind(comment)
//        holder.itemView.setOnClickListener { view ->
//            onClickAction(view, comment)
//        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}

class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val binding = LayoutCommentBinding.bind(itemView)

    fun bind(item: Comment) {
        binding.commentAuthor.text = item.author.displayName
        binding.datetime.text = item.postedDateTime
        binding.commentBody.text = item.body
        Timber.i("Comment bound!")
    }
}