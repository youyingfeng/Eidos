package org.eidos.reader.remote.requests

/**
 * A note about requests:
 * Just because a request is put under the model section does not mean that it is actually part of
 * the model. In reality, it is just a helper class to encapsulate search parameters.
 */
interface AutocompleteRequest {
    val absolutePath: String
}