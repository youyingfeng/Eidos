package org.eidos.reader.model

import java.net.URLEncoder

/**
 * This class should only be instantiated when passing a tag to the controller (and controller to remote, etc).
 * Propagation of tag information up to the view should be done using raw strings.
 */
data class Tag(val tagString: String) {
    fun getQueryString() : String {
        return URLEncoder.encode(tagString, "UTF-8")
    }
}