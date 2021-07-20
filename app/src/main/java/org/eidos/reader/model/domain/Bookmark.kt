package org.eidos.reader.model.domain

data class Bookmark
    constructor(
        val username: String,
        val tags: List<String>,
        val date: String,
        val notes: String,
        val bookmarkType: BookmarkType
    )
{
    enum class BookmarkType {
        RECOMMENDATION, PUBLIC, PRIVATE, HIDDEN
    }
}