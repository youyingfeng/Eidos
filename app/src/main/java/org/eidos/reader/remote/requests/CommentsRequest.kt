package org.eidos.reader.remote.requests

class CommentsRequest(
    val chapterID: Int,
    val pageNumber: Int
) {
    // TODO: Fill in the urlString parameter with the appropriate args
    val queryString = "/show_comments?chapter_id=$chapterID&page=$pageNumber"
}