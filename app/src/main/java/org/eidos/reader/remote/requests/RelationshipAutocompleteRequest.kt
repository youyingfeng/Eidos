package org.eidos.reader.remote.requests

import org.eidos.reader.remote.requests.AutocompleteRequest
import java.net.URLEncoder

class RelationshipAutocompleteRequest(searchTerm: String) : AutocompleteRequest {
    override val absolutePath: String = "/autocomplete/relationship?term=${URLEncoder.encode(searchTerm, "UTF-8")}"
}