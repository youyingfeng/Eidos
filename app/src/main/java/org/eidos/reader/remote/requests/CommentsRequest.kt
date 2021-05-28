package org.eidos.reader.remote.requests

class CommentsRequest(
    val chapterID: String,
    val pageNumber: Int
) {
    // TODO: Fill in the urlString parameter with the appropriate args
    val queryString = "/chapters/$chapterID?view_adult=true&show_comments=true&page=$pageNumber"
}