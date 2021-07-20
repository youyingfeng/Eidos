package org.eidos.reader.model.domain

data class BookmarksBlurb
    constructor(
        val item: ItemType,
        val bookmarks: List<Bookmark>
    )
{
    sealed class ItemType {
        data class WorkItem(val workBlurb: WorkBlurb): ItemType()
        data class SeriesItem(val seriesBlurb: SeriesBlurb): ItemType()
    }
}