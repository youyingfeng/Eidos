package org.eidos.reader.model.domain

data class CollectionBlurb
    constructor(
        val id: String,
        val name: String,
        val isOpen: Boolean,
        val isModerated: Boolean,
        val isRevealed: Boolean = true,
        val isAnonymous: Boolean = false,
        val challenge: Collection.Challenges,
        val summary: String,
        val maintainers: List<String>
    )
{

}
