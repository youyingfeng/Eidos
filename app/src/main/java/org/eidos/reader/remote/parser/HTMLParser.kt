package org.eidos.reader.remote.parser

import org.eidos.reader.model.Chapter
import org.eidos.reader.model.Work
import org.eidos.reader.model.WorkBlurb
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

class HTMLParser {
    companion object {
        /**
         * Extracts the list of works from the http file.
         * Selector syntax can be found at https://jsoup.org/cookbook/extracting-data/selector-syntax
         */
        fun parseWorksList(html: String) : List<WorkBlurb> {
            // may be able to implement without jsoup, but we use jsoup first
            val doc = Jsoup.parse(html)
            val workIndexList: Elements = doc.select("ol.work.index.group > li.work.blurb.group")

            // TODO: extract this out into a reusable function to extract work blurbs.
            val workBlurbList: List<WorkBlurb> = workIndexList.map { workIndex ->
                // declarations of subtrees beforehand to make assignment clearer
                val header = workIndex.select("h4.heading").select("a[href]")
                val fandomElements = workIndex.select("h5.fandoms.heading").select("a.tag")
                val requiredTags = workIndex.select("ul.required-tags").select("span.text")
                val userTags = workIndex.select("ul.tags.commas")
                val stats = workIndex.select("dl.stats")
                val chapterInfo = stats.select("dd.chapters").text().split("/")

                // actual work data
                val title = header.first().text()
                val authors = header.select("a[rel=author]").map { it.text() }
                val giftees = header.next().select(":not(a[rel=author])").map { it.text() }
                val datetime = workIndex.select("p.datetime").text()
                val fandoms = fandomElements.map { it.text() }
                val rating = requiredTags.get(0).text()
                val categories = requiredTags.get(2).text().split(", ")
                val completionStatus = requiredTags.get(3).text() == "Complete Work"    // boolean conversion
                val warnings = userTags.select("li.warnings").map { it.text() }
                val relationships = userTags.select("li.relationships").map { it.text() }
                val characters = userTags.select("li.characters").map { it.text() }
                val freeforms = userTags.select("li.freeforms").map { it.text() }
                val summary = workIndex.select("blockquote.userstuff.summary").text()
                val language = stats.select("dd.language").text()
                val words = stats.select("dd.words").text().replace(",","").toInt()
                val currentChapterCount = chapterInfo[0].replace(",","").toInt()
                val maxChapterCount = chapterInfo[1].toIntOrNull() ?: 0
                val comments = stats.select("dd.comments").text().toIntOrNull() ?: 0
                val kudos = stats.select("dd.kudos").text().toIntOrNull() ?: 0
                val bookmarks = stats.select("dd.bookmarks").text().toIntOrNull() ?: 0
                val hits = stats.select("dd.hits").text().toIntOrNull() ?: 0
                val workURL = header.first().attr("href")

                val workBlurb: WorkBlurb = WorkBlurb(
                    title = title,
                    authors = authors,
                    giftees = giftees,
                    lastUpdatedDate = datetime,
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
                    wordCount = words,
                    chapterCount = currentChapterCount,
                    maxChapters = maxChapterCount,
                    commentsCount = comments,
                    kudosCount = kudos,
                    bookmarksCount = bookmarks,
                    hitCount = hits,
                    workURL = workURL
                )

                return@map workBlurb
            }

            return workBlurbList
        }

        fun parseWorkAlternate(workHtml: String, navigationHtml: String, workURL: String) : Work {
            val workDoc = Jsoup.parse(workHtml)
            val navigationDoc = Jsoup.parse(navigationHtml)
            // get the list of URLs to zip with the contents later
            val chapterURLs : List<String> = navigationDoc
                    .select("div#main > ol.chapter.index.group > li > a[href]")
                    .map { it.attr("href") }

            // Statistics
            val metadataTree = workDoc.select("dl.work.meta.group")
            val statisticsTree = metadataTree.select("dl.stats")
            val chapterCountArray = statisticsTree.select("dd.chapters")
                    .first()
                    .text()
                    .split("/")

            val rating = metadataTree.select("dd.rating.tags")
                    .select("a.tag")
                    .first()
                    .text()
            val warnings = metadataTree.select("dd.warning.tags")
                    .select("a.tag")
                    .map { it.text() }
            val categories = metadataTree.select("dd.category.tags")
                    .select("a.tag")
                    .map { it.text() }
            val fandoms = metadataTree.select("dd.fandom.tags")
                    .select("a.tag")
                    .map { it.text() }
            val relationships = metadataTree.select("dd.relationship.tags")
                    .select("a.tag")
                    .map { it.text() }
            val characters = metadataTree.select("dd.character.tags")
                    .select("a.tag")
                    .map { it.text() }
            val freeforms = metadataTree.select("dd.freeform.tags")
                    .select("a.tag")
                    .map { it.text() }
            val language = metadataTree.select("dd.language")
                    .first()
                    .text()

            val publishDate = statisticsTree.select("dd.published")
                    .first()
                    .text()
            val lastUpdatedDate = statisticsTree.select("dd.status")
                    .first()
                    ?.text()
                    ?: publishDate
            val wordCount = statisticsTree.select("dd.words")
                    .first()
                    .text()
                    .toInt()
            val currentChapterCount = chapterCountArray[0].toInt()
            val maxChapterCount = chapterCountArray[1].toIntOrNull() ?: 0
            val completionStatus = currentChapterCount == maxChapterCount
            val comments = statisticsTree.select("dd.comments")
                    .first()
                    ?.text()
                    ?.toInt()
                    ?: 0
            val kudos = statisticsTree.select("dd.kudos")
                    .first()
                    ?.text()
                    ?.toInt()
                    ?: 0
            val bookmarks = statisticsTree.select("dd.bookmarks")
                    .first()
                    ?.text()
                    ?.toInt()
                    ?: 0
            val hits = statisticsTree.select("dd.hits")
                    .first()
                    .text()
                    .toInt()

            val title = workDoc
                    .select("div#workskin > div.preface.group > h2.title.heading")
                    .first()
                    .text()

            val summary = workDoc
                    .select("div#workskin > div.preface.group > div.summary.module > blockquote.userstuff")
                    .html()

            val authors = workDoc
                    .select("div#workskin > div.preface.group > h3.byline.heading > a[href]")
                    .map { it.text() }

            // TODO: Because Entire Work doesn't actually work on completed oneshots, we need to change chapterTrees

            val chapters : List<Chapter>

            if (currentChapterCount == 1 && maxChapterCount == 1) {
                // TODO: execute the following code if it is a oneshot
                val chapterText = workDoc.select("div#chapters > div.userstuff")
                        .first()
                        .html()

                val chapter = Chapter(
                        title = "",
                        summary = "",
                        preChapterNotes = "",
                        chapterBody = chapterText,
                        postChapterNotes = "",
                        chapterURL = chapterURLs[0]
                )

                chapters = listOf(chapter)

            } else {
                // TODO: Execute this branch if the work is a *completed* oneshot.
                // Get chapter trees
                val chapterTrees = workDoc.select("div#chapters > div.chapter")

                require(chapterTrees.size == chapterURLs.size)
                val zipChapterTreesAndUrlsList = chapterTrees zip chapterURLs
                chapters = zipChapterTreesAndUrlsList.map {
                    val title = it.first
                            .select("div.chapter.preface.group > h3.title")
                            .first()
                            .ownText()  // gets the text enclosed within the element that is *not* nested in other elements
                            .removePrefix(": ")
                    val summary = it.first
                            .select("div#summary > blockquote.userstuff")
                            .first()
                            ?.html()
                            ?: ""
                    val preChapterNotes = it.first
                            .select("div#notes > blockquote.userstuff")
                            .first()
                            ?.html()
                            ?: ""
                    val chapterText = it.first
                            .select("div.userstuff.module")
                            .first()
                            .apply { this.getElementById("work")?.remove() }
                            .html()
                    val postChapterNotes = it.first
                            .select("div.chapter.preface.group > div.end.notes.module > blockquote.userstuff")
                            .first()
                            ?.html()
                            ?: ""
                    val chapterURL = it.second

                    return@map Chapter(
                            title = title,
                            summary = summary,
                            preChapterNotes = preChapterNotes,
                            chapterBody = chapterText,
                            postChapterNotes = postChapterNotes,
                            chapterURL = chapterURL
                    )
                }
            }

            val preWorkNotes = getPreWorkNotes(workDoc)
            val postWorkNotes = getPostWorkNotes(workDoc)

            val work: Work = Work(
                    title = title,
                    authors = authors,
                    publishedDate = publishDate,
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
                    chapterCount = currentChapterCount,
                    maxChapters = maxChapterCount,
                    commentsCount = comments,
                    kudosCount = kudos,
                    bookmarksCount = bookmarks,
                    hitCount = hits,
                    workURL = workURL,
                    preWorkNotes = preWorkNotes,
                    chapters = chapters,
                    postWorkNotes = postWorkNotes
            )

            return work
        }

        /* Auxiliary small functions that should not be called from anywhere other than this class */
        private fun getPreWorkNotes(doc: Document) : String {
            val preWorkNotes : String = doc
                .select("#workskin > div.preface.group > div.notes.module > blockquote.userstuff")
                .first()
                ?.html()
                ?: ""

            return preWorkNotes
        }

        private fun getPostWorkNotes(doc: Document) : String {
            val postWorkNotes : String = doc
                .select("div#work_endnotes > blockquote.userstuff")
                .first()
                ?.html()
                ?: ""

            return postWorkNotes
        }

    }
}