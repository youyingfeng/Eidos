package org.eidos.reader.remote.requests

import org.eidos.reader.remote.requests.AutocompleteRequest
import java.net.URLEncoder

class RelationshipAutocompleteRequest(searchTerm: String) : AutocompleteRequest {
    override val queryString: String = "/relationship?term=${URLEncoder.encode(searchTerm, "UTF-8")}"
}