package org.eidos.reader.model.domain

data class Collection
    constructor(
        val id: String,
        val name: String,
        val isOpen: Boolean,
        val isModerated: Boolean,
        val isRevealed: Boolean = true,
        val isAnonymous: Boolean = false,
        val challenge: Challenges,
        val summary: String,
        val maintainers: List<String>,
        val subcollectionsCount: Int,
        val fandomsCount: Int,
        val worksCount: Int,
        val bookmarkedItemsCount: Int
    )
{
    // consider using extension functions to provide data sources instead

    enum class Challenges {
        GIFT, PROMPT, NONE
    }
}