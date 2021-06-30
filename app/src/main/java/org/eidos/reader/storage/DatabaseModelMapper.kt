package org.eidos.reader.storage

import org.eidos.reader.GetAllWorkBlurbs
import org.eidos.reader.ReadingListWorkBlurb
import org.eidos.reader.SavedWorkBlurb
import org.eidos.reader.model.domain.WorkBlurb

class DatabaseModelMapper {
    companion object {
        fun ReadingListWorkBlurb.toWorkBlurb(): WorkBlurb {
            with (this) {
                return WorkBlurb(
                    title = title,
                    authors = authors,
                    giftees = giftees ?: emptyList(),
                    lastUpdatedDate = lastUpdatedDate,
                    fandoms = fandoms,
                    rating = rating,
                    warnings = warnings,
                    categories = categories,
                    completionStatus = completionStatus,
                    characters = characters ?: emptyList(),
                    relationships = relationships ?: emptyList(),
                    freeforms = freeforms ?: emptyList(),
                    summary = summary,
                    language = language,
                    wordCount = wordCount,
                    chapterCount = chapterCount,
                    maxChapters = maxChapters,
                    commentsCount = 0,
                    kudosCount = 0,
                    bookmarksCount = 0,
                    hitCount = 0,
                    workURL = workURL
                )
            }
        }

        fun SavedWorkBlurb.toWorkBlurb(): WorkBlurb {
            with (this) {
                return WorkBlurb(
                    title = title,
                    authors = authors,
                    giftees = giftees ?: emptyList(),
                    lastUpdatedDate = lastUpdatedDate,
                    fandoms = fandoms,
                    rating = rating,
                    warnings = warnings,
                    categories = categories,
                    completionStatus = completionStatus,
                    characters = characters ?: emptyList(),
                    relationships = relationships ?: emptyList(),
                    freeforms = freeforms ?: emptyList(),
                    summary = summary,
                    language = language,
                    wordCount = wordCount,
                    chapterCount = chapterCount,
                    maxChapters = maxChapters,
                    commentsCount = 0,
                    kudosCount = 0,
                    bookmarksCount = 0,
                    hitCount = 0,
                    workURL = workURL
                )
            }
        }

        fun GetAllWorkBlurbs.toWorkBlurb(): WorkBlurb {
            with (this) {
                return WorkBlurb(
                    title = title,
                    authors = authors,
                    giftees = giftees ?: emptyList(),
                    lastUpdatedDate = lastUpdatedDate,
                    fandoms = fandoms,
                    rating = rating,
                    warnings = warnings,
                    categories = categories,
                    completionStatus = completionStatus,
                    characters = characters ?: emptyList(),
                    relationships = relationships ?: emptyList(),
                    freeforms = freeforms ?: emptyList(),
                    summary = summary,
                    language = language,
                    wordCount = wordCount,
                    chapterCount = chapterCount,
                    maxChapters = maxChapters,
                    commentsCount = 0,
                    kudosCount = 0,
                    bookmarksCount = 0,
                    hitCount = 0,
                    workURL = workURL
                )
            }
        }
    }
}