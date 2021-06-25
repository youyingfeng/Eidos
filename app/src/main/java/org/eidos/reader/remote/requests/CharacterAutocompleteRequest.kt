package org.eidos.reader.remote.requests

import org.eidos.reader.remote.requests.AutocompleteRequest
import java.net.URLEncoder

class CharacterAutocompleteRequest(searchTerm: String) : AutocompleteRequest {
    override val absolutePath: String = "/autocomplete/character?term=${URLEncoder.encode(searchTerm, "UTF-8")}"
}