package org.eidos.reader.remote.parser

import org.eidos.reader.model.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Entities
import org.jsoup.parser.Parser
import org.jsoup.select.Elements

class HTMLParser {
    /**
     * Extracts the list of works from the html file.
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
            val words = stats.select("dd.words")
                .text()
                .replace(",","")
                .let { if (it.isBlank()) 0 else it.toInt() }
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

    fun parseWorkBlurbFromWork(workHtml: String, workURL: String): WorkBlurb {
        // essentially copypasta from parseWork()
        val workDoc = Jsoup.parse(workHtml)
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

        val giftees = workDoc
            .select("#workskin > div.preface.group > div.notes.module > ul.associations > li > a")
            .map { it.text() }  // pseuds can be parsed based on names alone

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
            chapterCount = currentChapterCount,
            maxChapters = maxChapterCount,
            commentsCount = comments,
            kudosCount = kudos,
            bookmarksCount = bookmarks,
            hitCount = hits,
            workURL = workURL
        )
    }

    fun parseWork(workHtml: String, navigationHtml: String, workURL: String) : Work {
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

        val giftees = workDoc
            .select("#workskin > div.preface.group > div.notes.module > ul.associations > li > a")
            .map { it.text() }  // pseuds can be parsed based on names alone

        // TODO: Because Entire Work doesn't actually work on completed oneshots, we need to change chapterTrees

        val chapters : List<Chapter>

        if (currentChapterCount == 1 && maxChapterCount == 1) {
            // TODO: execute the following code if it is a completed oneshot
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

            chapters = listOf(chapter)  // singular list

        } else {
            // TODO: Execute this branch if the work is not a *completed* oneshot.
            // i.e. this is a multi-work series or incomplete single chapter work etc
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

        val preWorkNotes : String = workDoc
            .select("#workskin > div.preface.group > div.notes.module > blockquote.userstuff")
            .first()
            ?.html()
            ?: ""

        val postWorkNotes : String = workDoc
            .select("div#work_endnotes > blockquote.userstuff")
            .first()
            ?.html()
            ?: ""

        val workskin = workDoc.select("div#main > style[type='text/css']")
            .first()
            ?.html()
            ?: ""

        val work: Work = Work(
            title = title,
            authors = authors,
            giftees = giftees,
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
            postWorkNotes = postWorkNotes,
            workskin = workskin
        )

        return work
    }


    fun parseCommentsHTML(html: String) : List<Comment> {

        val doc = Jsoup.parseBodyFragment(html)
        val commentsPlaceholder = doc.getElementById("comments_placeholder")

        // ol.thread > li.comment gets all comments, but without info about their depth
        // body > ol.thread > li.comment gets top level lists (whether comment or thread)
//        val commentThreadObjects = doc.select("div#comments_placeholder > ol.thread > li")  // with hierarchy
        val commentThreadObjects = commentsPlaceholder.select("div#comments_placeholder > ol.thread > li")
        val commentObjects = commentsPlaceholder.select("ol.thread > li.comment") // flattened

//        println("comments = ${commentObjects?.size ?: 0}")

        val commentDepths = commentThreadObjects.map { it -> getCommentDepth(it) }

        // "more comments" links appear at the same depth as the last visible comment in a thread,
        // right below it.

        // IDEA: add the information about the hidden comments to the comment above the link,
        // and toss the link away
        // e.g. ... comment, link, ..., we put info from link into the comment before it, and
        // discard link

        /*
        Structure of link:
        <li.comment>
            <p>
                <a href="/comments/$PREVIOUS_COMMENT_ID">some string</a>
            </p>
        </li>

        Structure of actual comment:
        <li.comment.group.even>
            <h4>
                <a href>$USERNAME</a>
        </li>
        TODO: Refer to bottom for sample comment

         */

        val comments: MutableList<Comment> = mutableListOf<Comment>()
        for ((commentObject, depth) in commentObjects zip commentDepths) {
            // check for a href
            // if a href="/comments/$PREVIOUS_COMMENT_ID", this hides previous comments
            // else this refers to a non-anonymous user

            // if no a href then this is an anon user.
//            println(commentObject)

            val registeredUserHeaderElement = commentObject.selectFirst("h4.heading > a[href]")

            if (registeredUserHeaderElement != null) {
                // registered user
                // href already selected
                val userURL = registeredUserHeaderElement.attr("href")
                // URL must be of format /users/$USERNAME/pseuds/$PSEUDONYM
                val temp = userURL.removePrefix("/users/").split("/pseuds/")

                val author = Pseud(temp[0], temp[1])
                val chapter = commentObject.selectFirst("h4.heading > span.parent")
                    ?.ownText()
                    ?.removePrefix("on Chapter ")
                    ?.toInt()
                    ?: 1
                val datetime = commentObject.selectFirst("h4.heading > span.posted.datetime").text()
                val body = commentObject.selectFirst("li.comment > blockquote.userstuff").wholeText()
                val commentID = commentObject.id().removePrefix("comment_")

                val newComment = Comment(
                    commentID = commentID,
                    author = author,
                    chapter = chapter,
                    postedDateTime = datetime,
                    body = body,
                    commentDepth = depth
                )

                comments.add(newComment)
            } else {
                val anonymousUserHeaderElement = commentObject.selectFirst("h4.heading")

                if (anonymousUserHeaderElement != null) {
                    // anonymous user
                    // basically the same as the registered user case, except that the author changes
                    val username = anonymousUserHeaderElement.ownText()

                    val author = AnonymousUser(username)
                    val chapter = commentObject.selectFirst("h4.heading > span.parent")
                        ?.ownText()
                        ?.removePrefix("on Chapter ")
                        ?.toInt()
                        ?: 1
                    val datetime = commentObject.selectFirst("h4.heading > span.datetime").text()
                    val body = commentObject.selectFirst("li.comment > blockquote.userstuff").wholeText()
                    val commentID = commentObject.id().removePrefix("comment_")

                    val newComment = Comment(
                        commentID = commentID,
                        author = author,
                        chapter = chapter,
                        postedDateTime = datetime,
                        body = body,
                        commentDepth = depth
                    )

                    comments.add(newComment)
                } else {
                    // hidden comment
                    val numCommentsHidden = commentObject.selectFirst("p > a[href]")
                        .text() // Format: "11 more comments in this thread"
                        .removeSuffix(" more comments in this thread")
                        .toInt()

                    val previousComment = comments.last()

                    // replace last comment with new comment that has $numCommentsHidden
                    // hidden children
                    comments[comments.lastIndex] = previousComment.copy(
                        hasHiddenChildren = true,
                        numHiddenChildren = numCommentsHidden
                    )
                }
            }
        }

        return comments
    }

    /* Auxiliary small functions that should not be called from anywhere other than this class */

    private fun getCommentDepth(topLevelCommentHTML: Element) : Int {
        var currentDepth = 0
        var temp: Element? = topLevelCommentHTML

        while (temp != null) {
            temp = temp.selectFirst("ol.thread > li")
            currentDepth += 1
        }

        return currentDepth
        // currentDepth is guaranteed to be >= 1
    }
}

/*
<li class="comment group even" id="comment_282123784" role="article">
        <h4 class="heading byline">
                    <a href="/users/ASWF/pseuds/ASWF">ASWF</a>
          <span class="posted datetime">
            <abbr class="day" title="Tuesday">Tue</abbr> <span class="date">18</span>
                                                 <abbr class="month" title="February">Feb</abbr> <span class="year">2020</span>
                                                 <span class="time">07:20PM</span> <abbr class="timezone" title="Eastern Time (US &amp; Canada)">EST</abbr>
          </span>
        </h4>
        <div class="icon">
                  <a href="/users/ASWF/pseuds/ASWF"><img alt="" class="icon" src="https://s3.amazonaws.com/otw-ao3-icons/icons/285992/standard.PNG?1508376654"></a>
        </div>
        <blockquote class="userstuff"><p>Site owner pays site servers etc, knowing they need it for their site to work. </p><p>These apps sell fanfic. Content creators did not consent, and gain nothing. <br>App users pay in money, user data and seeing ads. </p><p>And i would not have consented anyway, even if someone asked (they did not) or granted me a contract (they did not). <br>I would've become a writer if i'd wanted to sell my words. </p><p>Also: other writers got their polite requests to take down their work rejected (see other comments). Only the DMCAs got through. Do not pretend that FPAL understands consent or the concept of artistic rights. </p><p>Whatever your personal interest in FPAL is, DMCAs do not go through for no reason. </p><p>i actually code professionally in real life, and hope to gods that this level of logic is not coming from a fellow colleague.</p></blockquote>
      <!-- end caching -->

      <h5 class="landmark heading">Comment Actions</h5>

<ul class="actions" id="navigation_for_comment_282123784">
    <li id="add_comment_reply_link_282123784"><a data-remote="true" href="/comments/add_comment_reply?admin_post_id=15103&amp;id=282123784&amp;page=2">Reply</a></li>
    <li><a href="/comments/282123784">Thread</a></li>
      <li>
        <a href="/comments/281866159">Parent Thread</a>
      </li>
</ul>

<!-- this is where the comment delete confirmation will be displayed if we have javascript -->
<!-- if not, here is where we will render the delete-comment form -->
  <div id="delete_comment_placeholder_282123784" style="display:none;">
  </div>

    <div id="add_comment_reply_placeholder_282123784" style="display: none;">
    </div>

</li>
 */