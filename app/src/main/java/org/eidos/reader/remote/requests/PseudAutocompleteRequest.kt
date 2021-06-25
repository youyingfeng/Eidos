package org.eidos.reader.remote.requests

import java.net.URLEncoder

class PseudAutocompleteRequest(searchTerm: String) : AutocompleteRequest {
    override val absolutePath: String = "/autocomplete/pseud?term=${URLEncoder.encode(searchTerm, "UTF-8")}"
}