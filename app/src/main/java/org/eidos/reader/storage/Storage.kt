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
import timber.log.Timber

class Storage(private val database: EidosDatabase) {
    private val workEntityQueries = database.workEntityQueries

    fun getWork(workURL: String): Work {
        return workEntityQueries.getWork(workURL, workMapper).executeAsOne()
    }

    fun getAllWorkBlurbs(): List<WorkBlurb> {
        val workBlurbs = workEntityQueries.getAllWorkBlurbs(workBlurbMapper).executeAsList()
        Timber.i("Number of works retrieved = ${workBlurbs.size}")
        return workBlurbs
    }

    fun insertWork(work: Work) {
        // TODO: make this an upsert? not important. probably needs an sql change.
        val savedWork = SavedWork(
            workURL = work.workURL,
            title = work.title,
            authors = work.authors,
            giftees = emptyList(),
            publishedDate = work.publishedDate,
            lastUpdatedDate = work.lastUpdatedDate,
            fandoms = work.fandoms,
            rating = work.rating,
            warnings = work.warnings,
            categories = work.categories,
            completionStatus = work.completionStatus,
            characters = work.characters,
            relationships = work.relationships,
            freeforms = work.freeforms,
            summary = work.summary,
            language = work.language,
            wordCount = work.wordCount,
            chapterCount = work.chapterCount,
            maxChapters = work.maxChapters,
            preWorkNotes = work.preWorkNotes,
            chapters = work.chapters,
            postWorkNotes = work.postWorkNotes,
            workskin = work.workskin
        )
        Timber.i("Storage: savedwork created")
        return workEntityQueries.insert(savedWork)
    }

    fun deleteWork(workURL: String) {
        return workEntityQueries.delete(workURL)
    }

    fun deleteAllWorks() {
        return workEntityQueries.deleteAll()
    }

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
                if (databaseValue.isBlank()) {
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
                completionStatus: Boolean,
                characters: List<String>?,
                relationships: List<String>?,
                freeforms: List<String>?,
                summary: String,
                language: String,
                wordCount: Int,
                chapterCount: Int,
                maxChapters: Int,
                preWorkNotes: String,
                chapters: List<Chapter>,
                postWorkNotes: String,
                workskin: String
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
                workURL = workURL,
                preWorkNotes = preWorkNotes ?: "",
                chapters = chapters,
                postWorkNotes = postWorkNotes ?: "",
                workskin = workskin ?: ""
            )
        }

        val workBlurbMapper = {
                workURL: String,
                title: String,
                authors: List<String>,
                giftees: List<String>?,
                lastUpdatedDate: String,
                fandoms: List<String>,
                rating: String,
                warnings: List<String>,
                categories: List<String>,
                completionStatus: Boolean,
                characters: List<String>?,
                relationships: List<String>?,
                freeforms: List<String>?,
                summary: String,
                language: String,
                wordCount: Int,
                chapterCount: Int,
                maxChapters: Int
            ->
            WorkBlurb(
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