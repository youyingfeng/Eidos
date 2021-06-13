package org.eidos.reader.model

data class WorkBlurb(
    val title: String,
    val authors: List<String>,
    val giftees: List<String>,
    val lastUpdatedDate: String,
    val fandoms: List<String>,
    val rating: String,
    val warnings: List<String>,
    val categories: List<String>,
    val completionStatus: Boolean,
    val characters: List<String>,
    val relationships: List<String>,
    val freeforms: List<String>,
    val summary: String,
    val language: String,
    val wordCount: Int,
    val chapterCount: Int,
    val maxChapters: Int,
    val commentsCount: Int,
    val kudosCount: Int,
    val bookmarksCount: Int,
    val hitCount: Int,
    val workURL: String
) {
    fun isSimilarTo(other: WorkBlurb): Boolean {
        return title == other.title
                && authors == other.authors
                && giftees == other.giftees
                && lastUpdatedDate == other.lastUpdatedDate
                && fandoms == other.fandoms
                && rating == other.rating
                && warnings == other.warnings
                && categories == other.categories
                && completionStatus == other.completionStatus
                && characters == other.characters
                && relationships == other.relationships
                && freeforms == other.freeforms
                && chapterCount == other.chapterCount
                && wordCount == other.wordCount
    }
}