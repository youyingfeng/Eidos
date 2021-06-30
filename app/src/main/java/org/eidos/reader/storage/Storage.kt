package org.eidos.reader.storage

import androidx.paging.PagingSource
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.android.paging3.QueryPagingSource
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import org.eidos.reader.*
import org.eidos.reader.model.domain.Chapter
import org.eidos.reader.model.domain.Work
import org.eidos.reader.model.domain.WorkBlurb
import org.eidos.reader.storage.DatabaseModelMapper.Companion.toWorkBlurb
import timber.log.Timber

class Storage(private val database: Database) {
    private val savedWorkQueries = database.savedWorkQueries

    val savedWorkBlurbs = savedWorkQueries.getAllWorkBlurbs()
        .asFlow()
        .mapToList()
        .map { list -> list.map { it.toWorkBlurb() } }

    fun getSavedWorkBlurbsPagingSource(): PagingSource<Long, SavedWorkBlurb> {
        return QueryPagingSource(
            countQuery = savedWorkQueries.countWorks(),
            transacter = savedWorkQueries,
            dispatcher = Dispatchers.IO,
            queryProvider = savedWorkQueries::savedWorkBlurb
        )
    }

    /* Methods for saved works */
    fun getWork(workURL: String): Work {
        return savedWorkQueries.getWork(workURL, workMapper).executeAsOne()
    }

    fun getAllWorkBlurbs(): List<WorkBlurb> {
        val workBlurbs = savedWorkQueries.getAllWorkBlurbs(workBlurbMapper).executeAsList()
        Timber.i("Number of works retrieved = ${workBlurbs.size}")
        return workBlurbs
    }

    fun insertWork(work: Work) {
        savedWorkQueries.upsert(
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
    }

    // updates works without forcefully reinserting missing works
    // the only reason why works are missing is because people delete them
    // this does not happen willy nilly
    fun updateWorks(updatedWorks: List<Work>) {
        savedWorkQueries.transaction {
            updatedWorks.forEach { work ->
                savedWorkQueries.update(
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
            }
        }
    }

    fun deleteWork(workURL: String) {
        return savedWorkQueries.delete(workURL)
    }

    fun deleteAllWorks() {
        return savedWorkQueries.deleteAll()
    }

    /* Methods for Reading History */
    private val readingHistoryQueries = database.readingHistoryQueries

    fun getReadingHistory(): List<WorkBlurb> {
        val workBlurbs = readingHistoryQueries.getAllWorkBlurbs(workBlurbMapper).executeAsList()
        return workBlurbs
    }

    fun addWorkBlurbToReadingHistory(work: WorkBlurb) {
        readingHistoryQueries.upsertToEnd(
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
    }

    fun deleteWorkBlurbFromReadingHistory(workURL: String) {
        readingHistoryQueries.delete(workURL)
    }

    /* Methods for Reading List */
    private val readingListQueries = database.readingListQueries

    fun getReadingListWorkBlurbsPagingSource(): PagingSource<Long, ReadingListWorkBlurb> {
        return QueryPagingSource(
            countQuery = readingListQueries.countWorkBlurbs(),
            transacter = readingListQueries,
            dispatcher = Dispatchers.IO,
            queryProvider = readingListQueries::readingListWorkBlurb
        )
    }

    fun getReadingList(): List<WorkBlurb> {
        val workBlurbs = readingListQueries.getAllWorkBlurbs(workBlurbMapper).executeAsList()
        return workBlurbs
    }

    fun addWorkBlurbToReadingList(work: WorkBlurb) {
        readingListQueries.upsertToEnd(
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