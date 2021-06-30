package org.eidos.reader.remote

import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.eidos.reader.model.domain.Work
import org.eidos.reader.model.domain.WorkBlurb
import org.eidos.reader.network.Network
import org.eidos.reader.model.domain.Comment
import org.eidos.reader.model.domain.WorkSearchMetadata
import org.eidos.reader.remote.parser.HTMLParser
import org.eidos.reader.remote.requests.AutocompleteRequest
import org.eidos.reader.remote.requests.CommentsRequest
import org.eidos.reader.remote.requests.WorkFilterRequest
import org.eidos.reader.remote.requests.WorkRequest

private const val ARCHIVE_BASE_URL = "https://archiveofourown.org"

class AO3
    constructor(
        private val network: Network,
        private val parser: HTMLParser
    )
{

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    /**
     * Retrieves the number of works and the name of the tag from the specified page.
     *
     * Throws [Network.RedirectException] if a redirect is encountered, which indicates that
     * one of the following scenarios has occured:
     *
     * 1. The tag of the [workFilterRequest] is a synonym of some other tag. The locationUrl will
     * end with "/works".
     * 2. The tag cannot be filtered on. The locationUrl will not end with "/works".
     *
     * Throws [Network.ServerException] if AO3 cannot resolve the request.
     *
     * Throws [Network.NetworkException] if the connection is bad.
     */
    fun getWorkSearchMetadata(workFilterRequest: WorkFilterRequest): WorkSearchMetadata {
        val urlString = ARCHIVE_BASE_URL + workFilterRequest.absolutePath
        val responseBody = try {
            network.get(urlString)
        } catch (e: Network.RedirectException) {
            val tokens = e.locationUrl.split("/")
            if (tokens.last() == "works") {
                val redirectedTagName = WorkFilterRequest.decodeMainTag(tokens[tokens.lastIndex - 1])
                throw TagSynonymException(redirectedTagName = redirectedTagName)
            } else {
                throw TagNotFilterableException()
            }
        }
        return parser.parseMetadata(responseBody)
    }

    /**
     * Retrieves a list of up to 20 [WorkBlurb]s based on the parameters passed
     * in the [workFilterRequest].
     *
     * Throws [Network.RedirectException] if a redirect is encountered, which indicates that
     * one of the following scenarios has occured:
     *
     * 1. The tag of the [workFilterRequest] is a synonym of some other tag. The locationUrl will
     * end with "/works".
     * 2. The tag cannot be filtered on. The locationUrl will not end with "/works".
     *
     * Throws [Network.ServerException] if AO3 cannot resolve the request.
     *
     * Throws [Network.NetworkException] if the connection is bad.
     */
    fun getWorkBlurbs(workFilterRequest: WorkFilterRequest): List<WorkBlurb> {
        val urlString = ARCHIVE_BASE_URL + workFilterRequest.absolutePath
        val responseBody = network.get(urlString)
        return parser.parseWorksList(responseBody)
    }

    /**
     * Retrieves a [Work] at the location indicated in the [workRequest].
     *
     * Throws [Network.ServerException] if AO3 cannot resolve the request.
     *
     * Throws [Network.NetworkException] if the connection is bad.
     */
    fun getWork(workRequest: WorkRequest): Work {
        // Input: the navigation page of the work
        // Output: the work itself

        val workUrl = ARCHIVE_BASE_URL + workRequest.viewEntireWorkAbsolutePath
        val navigationIndexUrl = ARCHIVE_BASE_URL + workRequest.navigationAbsolutePath

        val workResponseBody = network.get(workUrl)
        val navigationIndexResponseBody = network.get(navigationIndexUrl)
        return parser.parseWork(
                workHtml = workResponseBody,
                navigationHtml = navigationIndexResponseBody,
                workURL = workRequest.absolutePath
        )

    }

    /**
     * Extracts a [WorkBlurb] from the [Work] indicated in the [workRequest].
     *
     * TODO: consider cases where server cannot resolve location/authentication fails
     *
     * Throws [Network.NetworkException] if the connection is bad.
     */
    fun getWorkBlurbFromWork(workRequest: WorkRequest): WorkBlurb {
        val workUrl = ARCHIVE_BASE_URL + workRequest.viewFirstChapterAbsolutePath
        val workResponseBody = network.get(workUrl)
        return parser.parseWorkBlurbFromWork(workResponseBody, workRequest.absolutePath)
    }

    /**
     * Retrieves a list of up to 15 tags from AO3's autocomplete API.
     *
     * Throws [Network.NetworkException] if the connection is bad.
     */
    fun getAutocompleteResults(autocompleteRequest: AutocompleteRequest): List<String> {
        val autocompleteUrl = ARCHIVE_BASE_URL + autocompleteRequest.absolutePath
        println(autocompleteUrl)

        val responseBody: String = network.getJSON(autocompleteUrl)

        val listResultType = Types.newParameterizedType(List::class.java, AutocompleteResult::class.java)
        val jsonAdapter: JsonAdapter<List<AutocompleteResult>> = moshi.adapter(listResultType)

        // lmao what is performance
        // honestly this is really not performant code but it works
        // TODO: VVVVV low priority - eliminate the intermediate conversion to AutocompleteResult
        val autocompleteResults: List<AutocompleteResult> = jsonAdapter.fromJson(responseBody) ?: emptyList()
        val results = autocompleteResults.map { it.name }

        return results
    }

    /**
     * Retrieves a list of comments based on the chapter and page number passed in [commentsRequest].
     *
     * Throws [Network.NetworkException] if the connection is bad.
     */
    fun getComments(commentsRequest: CommentsRequest): List<Comment> {
        val commentsUrl = "https://archiveofourown.org${commentsRequest.absolutePath}"

        val responseBody: String = network.get(commentsUrl)

        return parser.parseCommentsHTML(responseBody)
    }

    private class AutocompleteResult(
        val id: String,
        val name: String
    )

    /* Exceptions */
    class TagSynonymException(message: String = "This tag is a synonym of another tag.", val redirectedTagName: String) : Exception(message)
    class TagNotFilterableException(message: String = "This tag is not marked common, and cannot be filtered on.") : Exception(message)
}