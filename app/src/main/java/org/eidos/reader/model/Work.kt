package org.eidos.reader.model

import org.eidos.reader.model.Chapter

data class Work(
    // old fields taken from workblurb
        val title: String,
        val authors: List<String>,
        val giftees: List<String>,
        val publishedDate: String,
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
        val workURL: String,
    // these are new fields
        val preWorkNotes: String,
        val chapters: List<Chapter>,
        val postWorkNotes: String,
        val workskin: String
) {
    fun getWorkBlurb(): WorkBlurb {
        return WorkBlurb(
            title = title,
            authors = authors,
            giftees = giftees,
            lastUpdatedDate = lastUpdatedDate,
            fandoms = fandoms,
            rating = rating,
            warnings = warnings,
            categories = categories,
            completionStatus = completionStatus,
            characters = characters,
            relationships = relationships,
            freeforms = freeforms,
            summary = summary,
            language = language,
            wordCount = wordCount,
            chapterCount = chapterCount,
            maxChapters = maxChapters,
            commentsCount = commentsCount,
            kudosCount = kudosCount,
            bookmarksCount = bookmarksCount,
            hitCount = hitCount,
            workURL = workURL
        )
    }
}