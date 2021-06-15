package org.eidos.reader.remote

import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.eidos.reader.model.Work
import org.eidos.reader.model.WorkBlurb
import org.eidos.reader.network.Network
import org.eidos.reader.model.Comment
import org.eidos.reader.model.WorkSearchMetadata
import org.eidos.reader.remote.parser.HTMLParser
import org.eidos.reader.remote.requests.AutocompleteRequest
import org.eidos.reader.remote.requests.CommentsRequest
import org.eidos.reader.remote.requests.WorkFilterRequest
import org.eidos.reader.remote.requests.WorkRequest

class AO3
    constructor(
        private val network: Network,
        private val parser: HTMLParser
    )
{

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    fun getWorkSearchMetadata(workFilterRequest: WorkFilterRequest): WorkSearchMetadata {
        val urlString = "https://archiveofourown.org" + workFilterRequest.queryString
        val responseBody = network.get(urlString)
        return parser.parseMetadata(responseBody)
    }

    fun getWorkBlurbs(workFilterRequest: WorkFilterRequest): List<WorkBlurb> {
        val urlString = "https://archiveofourown.org" + workFilterRequest.queryString
        println(urlString)

        try {
            val responseBody = network.get(urlString)
            return parser.parseWorksList(responseBody)
        } catch (e: Network.NetworkException) {
            print(e.message)
            throw e
        }
    }

    fun getWork(workRequest: WorkRequest): Work {
        // Input: the navigation page of the work
        // Output: the work itself

        val workUrlString = "https://archiveofourown.org" + workRequest.getEntireWorkURL()
        val navigationIndexUrlString = "https://archiveofourown.org" +
                workRequest.getNavigationIndexPageURL()

        try {
            val workResponseBody = network.get(workUrlString)
            val navigationIndexResponseBody = network.get(navigationIndexUrlString)
            return parser.parseWork(
                    workHtml = workResponseBody,
                    navigationHtml = navigationIndexResponseBody,
                    workURL = workRequest.url
            )
        } catch (e: Network.NetworkException) {
            throw e
        }
    }

    fun getWorkBlurbFromWork(workRequest: WorkRequest): WorkBlurb {
        val workUrlString = "https://archiveofourown.org" + workRequest.getWorkURL()
        val workResponseBody = network.get(workUrlString)
        return parser.parseWorkBlurbFromWork(workResponseBody, workRequest.url)
    }

    fun getAutocompleteResults(autocompleteRequest: AutocompleteRequest): List<String> {
        val urlString = "https://archiveofourown.org/autocomplete" + autocompleteRequest.queryString
        println(urlString)

        val responseBody: String = try {
            network.getJSON(urlString)
        } catch (e: Network.NetworkException) {
            throw e
        }

        // TODO: parse the JSON results into a map/array - usually array should be enough
        // Using Moshi, parse the json results. its easy. return as a list<string>


        val listResultType = Types.newParameterizedType(List::class.java, AutocompleteResult::class.java)
        val jsonAdapter: JsonAdapter<List<AutocompleteResult>> = moshi.adapter(listResultType)

        // lmao what is performance
        // honestly this is really not performant code but it works
        // TODO: VVVVV low priority - eliminate the intermediate conversion to AutocompleteResult
        val autocompleteResults: List<AutocompleteResult> = jsonAdapter.fromJson(responseBody) ?: emptyList()
        val results = autocompleteResults.map { it.name }
        results.forEach {
            println(it)
        }

        return results
    }

    fun getComments(commentsRequest: CommentsRequest): List<Comment> {
        val urlString = "https://archiveofourown.org${commentsRequest.queryString}"

        val responseBody: String = try {
            network.get(urlString)
        } catch (e: Network.NetworkException) {
            throw e
        }

//        println(responseBody)

        return parser.parseCommentsHTML(responseBody)
    }

    private class AutocompleteResult(
        val id: String,
        val name: String
    )
}