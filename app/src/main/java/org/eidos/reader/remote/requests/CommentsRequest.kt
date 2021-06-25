package org.eidos.reader.remote.requests

class CommentsRequest(
    val chapterID: String,
    val pageNumber: Int
) {
    val absolutePath = "/chapters/$chapterID?view_adult=true&show_comments=true&page=$pageNumber"
}