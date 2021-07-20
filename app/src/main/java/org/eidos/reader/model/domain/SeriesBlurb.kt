package org.eidos.reader.model.domain

data class SeriesBlurb
    constructor(
        val id: Long,
        val title: String,
        val author: String,     // convert to username?
        val lastUpdatedDate: String,    // convert to java date
        val fandoms: List<String>,
        val relationships: List<String>,
        val characters: List<String>,
        val freeforms: List<String>,
        val wordCount: Int,
        val worksCount: Int
    )
{
}