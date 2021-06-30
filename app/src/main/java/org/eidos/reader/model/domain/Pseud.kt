package org.eidos.reader.model.domain

class Pseud
    constructor (
        val username: String,
        val pseudonym: String
    ) : User
{
    override val displayName: String
        get() = "$pseudonym ($username)"
}
