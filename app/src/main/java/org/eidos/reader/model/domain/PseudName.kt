package org.eidos.reader.model.domain

class PseudName
    constructor (
        val username: String,
        val pseudonym: String
    ) : UserName
{
    override val displayName: String
        get() = "$pseudonym ($username)"
}
