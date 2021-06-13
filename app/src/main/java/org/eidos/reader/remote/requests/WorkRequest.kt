package org.eidos.reader.remote.requests

class WorkRequest(path: String) {
    // queryString should be only /works/<id>/navigate
    // view_adult = true will also be added at a later part
    val queryString = path + "/navigate"
    val url = path

    fun getNavigationIndexPageURL() = url + "/navigate"
    fun getEntireWorkURL() = url + "?view_adult=true&view_full_work=true"
    fun getWorkURL() = url + "?view_adult=true"
}