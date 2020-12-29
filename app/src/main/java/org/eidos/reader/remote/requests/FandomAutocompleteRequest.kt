package org.eidos.reader.remote.requests

import org.eidos.reader.remote.requests.AutocompleteRequest
import java.net.URLEncoder

class FandomAutocompleteRequest(searchTerm: String) : AutocompleteRequest {
    override val queryString: String = "/fandom?term=${URLEncoder.encode(searchTerm, "UTF-8")}"
}