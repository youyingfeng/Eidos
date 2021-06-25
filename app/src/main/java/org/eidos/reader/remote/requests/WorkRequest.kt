package org.eidos.reader.remote.requests

/**
 * Represents the absolute paths involved in retrieving the work at the specified [absolutePath].
 *
 * [absolutePath] should be of the format /works/$id
 */
class WorkRequest(val absolutePath: String) {
    val navigationAbsolutePath = "$absolutePath/navigate"
    val viewEntireWorkAbsolutePath = "$absolutePath?view_adult=true&view_full_work=true"
    val viewFirstChapterAbsolutePath = "$absolutePath?view_adult=true"
}