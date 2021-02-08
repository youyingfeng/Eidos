package org.eidos.reader.remote.requests

import java.net.URLEncoder

class TagAutocompleteRequest(searchTerm: String) : AutocompleteRequest {
    override val queryString: String = "/tag?term=${URLEncoder.encode(searchTerm, "UTF-8")}"
}