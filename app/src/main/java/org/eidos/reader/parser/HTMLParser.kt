package org.eidos.reader.parser

import org.eidos.reader.model.Chapter
import org.eidos.reader.model.Work
import org.eidos.reader.model.WorkBlurb
import org.eidos.reader.network.Network
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.eidos.reader.remote.requests.ChapterRequest

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

        fun parseChapter(parseTree: Document) : Chapter {
            // this only parses a single chapter
            // parseTree represents the html of the entire page.
            val workTree = parseTree.select("div#workskin")
            val chapterTree = workTree.select("div#chapters > div.chapter")

            // TODO: Refactor this out into another function
            /*
            IMPORTANT NOTE:
            the format of the html obtained at /work/<id> is different from the format obtained
            at /work/<id>/chapter/<id>

            Thankfully, oneshots allow for both formats.

            Make sure that the first format does not appear ANYWHERE near this function
             */
            // FIXME: be stricter with summaries and notes, bc this code will mix work notes and chapter notes
            // FIXME: same above with summaries - make sure work summary is not mixed with chapter summary
            // TODO: check if chapter 1 can have 2 summaries - since it can have 2 notes.
            val title = chapterTree.select("div.chapter.preface.group > h3.title")
                .first()
                .text()
            val summary = chapterTree.select("div#summary > blockquote.userstuff")
                .first()
                ?.html()
                ?: ""
            val preChapterNotes = chapterTree.select("div#notes > blockquote.userstuff") // FIXME: verify
                .first()
                ?.html()
                ?: ""
            val chapterText = chapterTree.select("div.userstuff.module")
                .first()
                .html()
            val postChapterNotes = chapterTree
                .select("div.chapter.preface.group > div.end.notes.module > blockquote.userstuff")
                .first()
                ?.html()
                ?: ""

            val chapter = Chapter(
                title = title,
                summary = summary,
                preChapterNotes = preChapterNotes,
                chapterBody = chapterText,
                postChapterNotes = postChapterNotes
            )

            return chapter
        }

        fun parseWork(html: String, workURL: String) : Work {
            val doc = Jsoup.parse(html)
            // optimise later, get something working first
            val chapterURLs : List<String> = doc.select("div#main > ol.chapter.index.group > li > a[href]")
                .map { it.attr("href") }

            val chapterParseTrees : List<Document> = chapterURLs
                .map { url: String -> ChapterRequest(url) }
                .map { chapterRequest: ChapterRequest -> getChapterHTML(chapterRequest) }
                .map { Jsoup.parse(it) }


            val firstChapter = chapterParseTrees[0]
            val metadataTree = firstChapter.select("dl.work.meta.group")
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
            val hits = statisticsTree.select("dd.comments")
                .first()
                .text()
                .toInt()

            val title = firstChapter
                .select("div#workskin > div.preface.group > h2.title.heading")
                .first()
                .text()

            val summary = firstChapter
                .select("div#workskin > div.preface.group > div.summary.module > blockquote.userstuff")
                .html()

            val authors = firstChapter
                .select("div#workskin > div.preface.group > h3.byline.heading > a[href]")
                .map { it.text() }

            val chapters = chapterParseTrees.map { parseTree: Document -> parseChapter(parseTree) }

            val preWorkNotes = getPreWorkNotes(chapterParseTrees[0])
            val postWorkNotes = getPostWorkNotes((chapterParseTrees[chapterParseTrees.lastIndex]))

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

        private fun getChapterHTML(chapterRequest: ChapterRequest) : String {
            val urlString = "https://www.archiveofourown.org" + chapterRequest.queryString
            println(urlString)

            val html = try {
                Network.get(urlString)
            } catch (e: Network.NetworkException) {
                throw e
            }

            return html
        }

        private fun getMetadata(html: String) {
            // just return a workblurb bc its literally the same thing
            // to be called on the very first chapter only, bc its guaranteed to contain the work summary



        }

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