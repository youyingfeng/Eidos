package org.eidos.reader.remote.requests

import org.eidos.reader.remote.requests.AutocompleteRequest
import java.net.URLEncoder

class FreeformAutocompleteRequest(searchTerm: String) : AutocompleteRequest {
    override val absolutePath: String = "/autocomplete/freeform?term=${URLEncoder.encode(searchTerm, "UTF-8")}"
}