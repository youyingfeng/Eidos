package org.eidos.reader.storage

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import org.eidos.reader.Database
import org.eidos.reader.ReadingHistoryWorkBlurb
import org.eidos.reader.ReadingListWorkBlurb
import org.eidos.reader.SavedWork
import org.eidos.reader.model.Chapter
import org.eidos.reader.model.Work
import org.eidos.reader.model.WorkBlurb
import timber.log.Timber

class Storage(private val database: Database) {
    private val workEntityQueries = database.workEntityQueries

    /* Methods for saved works */
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
            giftees = work.giftees,
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
//        Timber.i("Storage: savedwork created")
        return workEntityQueries.insert(savedWork)
    }

    fun deleteWork(workURL: String) {
        return workEntityQueries.delete(workURL)
    }

    fun deleteAllWorks() {
        return workEntityQueries.deleteAll()
    }

    /* Methods for Reading History */
    private val readingHistoryQueries = database.readingHistoryQueries

    fun getReadingHistory(): List<WorkBlurb> {
        val workBlurbs = readingHistoryQueries.getAllWorkBlurbs(workBlurbMapper).executeAsList()
        return workBlurbs
    }

    fun addWorkBlurbToReadingHistory(work: WorkBlurb) {
        val readingHistoryWorkBlurb = ReadingHistoryWorkBlurb(
            workURL = work.workURL,
            title = work.title,
            authors = work.authors,
            giftees = work.giftees,
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
            maxChapters = work.maxChapters
        )

        readingHistoryQueries.transaction {
            readingHistoryQueries.deleteIfExists(readingHistoryWorkBlurb.workURL)
            readingHistoryQueries.insertIfAbsent(readingHistoryWorkBlurb)
        }
    }

    fun deleteWorkBlurbFromReadingHistory(workURL: String) {
        readingHistoryQueries.delete(workURL)
    }

    /* Methods for Reading List */
    private val readingListQueries = database.readingListQueries

    fun getReadingList(): List<WorkBlurb> {
        val workBlurbs = readingListQueries.getAllWorkBlurbs(workBlurbMapper).executeAsList()
        return workBlurbs
    }

    fun addWorkBlurbToReadingList(work: WorkBlurb) {
        val readingListWorkBlurb = ReadingListWorkBlurb(
            workURL = work.workURL,
            title = work.title,
            authors = work.authors,
            giftees = work.giftees,
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
            maxChapters = work.maxChapters
        )

        readingListQueries.transaction {
            // FIXME: this shit does not work! delete throws an error!
            readingListQueries.deleteIfExists(readingListWorkBlurb.workURL)
            readingListQueries.insertIfAbsent(readingListWorkBlurb)
        }
    }

    fun deleteWorkBlurbFromReadingList(workURL: String) {
        readingListQueries.delete(workURL)
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

        val savedWorkAdapter = SavedWork.Adapter(
            authorsAdapter = Storage.listOfStringsAdapter,
            gifteesAdapter = Storage.listOfStringsAdapter,
            fandomsAdapter = Storage.listOfStringsAdapter,
            warningsAdapter = Storage.listOfStringsAdapter,
            categoriesAdapter = Storage.listOfStringsAdapter,
            charactersAdapter = Storage.listOfStringsAdapter,
            relationshipsAdapter = Storage.listOfStringsAdapter,
            freeformsAdapter = Storage.listOfStringsAdapter,
            chaptersAdapter = Storage.chaptersAdapter
        )

        val readingHistoryWorkBlurbAdapter = ReadingHistoryWorkBlurb.Adapter(
            authorsAdapter = Storage.listOfStringsAdapter,
            gifteesAdapter = Storage.listOfStringsAdapter,
            fandomsAdapter = Storage.listOfStringsAdapter,
            warningsAdapter = Storage.listOfStringsAdapter,
            categoriesAdapter = Storage.listOfStringsAdapter,
            charactersAdapter = Storage.listOfStringsAdapter,
            relationshipsAdapter = Storage.listOfStringsAdapter,
            freeformsAdapter = Storage.listOfStringsAdapter,
        )

        val readingListWorkBlurbAdapter = ReadingListWorkBlurb.Adapter(
            authorsAdapter = Storage.listOfStringsAdapter,
            gifteesAdapter = Storage.listOfStringsAdapter,
            fandomsAdapter = Storage.listOfStringsAdapter,
            warningsAdapter = Storage.listOfStringsAdapter,
            categoriesAdapter = Storage.listOfStringsAdapter,
            charactersAdapter = Storage.listOfStringsAdapter,
            relationshipsAdapter = Storage.listOfStringsAdapter,
            freeformsAdapter = Storage.listOfStringsAdapter,
        )

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
                giftees = giftees ?: emptyList(),
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