package org.eidos.reader.remote.requests

class WorkRequest(path: String) {
    // queryString should be only /works/<id>/navigate
    // view_adult = true will also be added at a later part
    val queryString = path + "/navigate"
    val url = path
}