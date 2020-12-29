package org.eidos.reader.model

data class Chapter(
    // should i include url here???
    val title: String,
    val summary: String,
    val preChapterNotes: String,
    val chapterBody: String,
    val postChapterNotes: String
) {

}