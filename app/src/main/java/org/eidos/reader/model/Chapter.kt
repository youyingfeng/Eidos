package org.eidos.reader.model

data class Chapter(
    // should i include url here???
    val title: String,
    val summary: String,
    val preChapterNotes: String,
    val chapterBody: String,
    val postChapterNotes: String,
    val chapterURL : String
) {
    // gets the part after "/chapters/"
    val chapterID: String = chapterURL.split("/chapters/")[1]
}