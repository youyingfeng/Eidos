package org.eidos.reader.storage

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import org.eidos.reader.EidosDatabase
import org.eidos.reader.SavedWork
import org.eidos.reader.model.Chapter
import org.eidos.reader.model.Work
import org.eidos.reader.model.WorkBlurb

class Storage(database: EidosDatabase) {
    companion object {
        // moshi setup
        private val moshi: Moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
        private val listChapterType = Types.newParameterizedType(List::class.java, Chapter::class.java)
        private val jsonAdapter: JsonAdapter<List<Chapter>> = moshi.adapter(listChapterType)

        // Adapters
        val listOfStringsAdapter = object : ColumnAdapter<List<String>, String> {
            override fun decode(databaseValue: String) =
                if (databaseValue.isEmpty()) {
                    listOf()
                } else {
                    databaseValue.split(",")
                }

            override fun encode(value: List<String>) = value.joinToString(separator = ",")
        }

        val chaptersAdapter = object : ColumnAdapter<List<Chapter>, String> {
            override fun decode(databaseValue: String) = jsonAdapter.fromJson(databaseValue)!!

            override fun encode(value: List<Chapter>) = jsonAdapter.toJson(value)

        }

        // custom mappers
        val workMapper = {
                workURL: String,
                title: String,
                authors: List<String>,
                giftees: List<String>?,
                publishedDate: String,
                lastUpdatedDate: String,
                fandoms: List<String>,
                rating: String,
                warnings: List<String>,
                categories: List<String>,
                completionStatus: Int,
                characters: List<String>?,
                relationships: List<String>?,
                freeforms: List<String>?,
                summary: String,
                language: String,
                wordCount: Int,
                chapterCount: Int,
                maxChapters: Int,
                preWorkNotes: String?,
                chapters: List<Chapter>,
                postWorkNotes: String?,
                workskin: String?
                ->
            Work(
                title = title,
                authors = authors,
                publishedDate = publishedDate,
                lastUpdatedDate = lastUpdatedDate,
                fandoms = fandoms,
                rating = rating,
                warnings = warnings,
                categories = categories,
                completionStatus = completionStatus != 0,
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
                workURL = workURL,
                preWorkNotes = preWorkNotes ?: "",
                chapters = chapters,
                postWorkNotes = postWorkNotes ?: "",
                workskin = workskin ?: ""
            )
        }

//        val workBlurbMapper = {
//                workURL: String,
//                title: String,
//                authors: List<String>,
//                lastUpdatedDate: String,
//                fandoms: List<String>,
//                rating: String,
//                warnings: List<String>,
//                categories: List<String>,
//                summary: String,
//                language: String,
//                wordCount: Int,
//                chapterCount: Int,
//                maxChapters: Int
//                ->
//            WorkBlurb(
//                title = title,
//                authors = authors,
//                giftees = emptyList(),
//                lastUpdatedDate = lastUpdatedDate,
//                fandoms = fandoms,
//                rating = rating,
//                warnings = warnings,
//                categories = categories,
//                completionStatus = chapterCount == maxChapters,
//                characters = characters ?: emptyList(),
//                relationships = relationships,
//                freeforms = freeforms,
//                summary = summary,
//                language = language,
//                wordCount = words,
//                chapterCount = currentChapterCount,
//                maxChapters = maxChapterCount,
//                commentsCount = comments,
//                kudosCount = kudos,
//                bookmarksCount = bookmarks,
//                hitCount = hits,
//                workURL = workURL
//            )
//        }
    }

    fun getWork(workURL: String): Work {
        TODO("Not implemented yet")
    }
}