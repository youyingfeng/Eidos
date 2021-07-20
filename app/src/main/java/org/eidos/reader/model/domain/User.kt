package org.eidos.reader.model.domain

data class User
    constructor(
        val username: String,
        val pseudname: String?,
        val pseuds: List<String>,
        val fandoms: List<String>,
        val worksCount: Int,
        val seriesCount: Int,
        val bookmarksCount: Int,
        val collectionsCount: Int,
        val giftsCount: Int
    )
{
    val userPath = "/users/$username${if (pseudname != null) "/pseuds/$pseudname" else ""}"
    // should the URL be in the model?

    // TODO: include paging data sources for works, series, bookmarks, collections, gifts
    // maybe this can just represent the "info" or "metadata" in the domain layer,
    // and the data sources can be included in the data/ui layer?
}