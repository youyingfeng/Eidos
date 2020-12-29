package org.eidos.reader.remote.requests

class ChapterRequest(path: String) {
    val queryString = path + "?view_adult=true"
}