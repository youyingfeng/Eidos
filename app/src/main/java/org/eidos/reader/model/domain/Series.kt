package org.eidos.reader.model.domain

data class Series
    constructor(
        val id: String, // can this be a long?
        val creators: List<String>,
        val beginDate: String,
        val lastUpdatedDate: String,
        val wordCount: Int,
        val worksCount: Int,
        val completionStatus: Boolean,
        val bookmarksCount: Int
    )
{
    // actual work can be represented using data sources, but mayhaps in another
    // model class?
}