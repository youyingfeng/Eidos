package org.eidos.reader.model.domain

data class Comment(
    val commentID: String,
    val author: User,
    val chapter: Int,
    val postedDateTime: String,
    val body: String,
    val commentDepth: Int,
    val hasHiddenChildren: Boolean = false,
    val numHiddenChildren: Int = 0
) {
}