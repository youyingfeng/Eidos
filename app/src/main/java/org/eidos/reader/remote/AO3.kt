package org.eidos.reader.remote

import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.eidos.reader.model.Work
import org.eidos.reader.model.WorkBlurb
import org.eidos.reader.network.Network
import okhttp3.OkHttpClient
import org.eidos.reader.remote.parser.HTMLParser
import org.eidos.reader.remote.requests.AutocompleteRequest
import org.eidos.reader.remote.requests.WorkFilterRequest
import org.eidos.reader.remote.requests.WorkRequest

class AO3 {
    companion object {
        private val moshi: Moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()

        fun getWorkBlurbs(workListRequest: WorkFilterRequest) : List<WorkBlurb> {
            // TODO: send request to okhttp3
            val urlString = "https://archiveofourown.org" + workListRequest.queryString
            println(urlString)

            try {
                val responseBody = Network.get(urlString)
                return HTMLParser.parseWorksList(responseBody)
            } catch (e: Network.NetworkException) {
                print(e.message)
                throw e
            }
        }

        fun getWork(workRequest: WorkRequest) : Work {
            // Input: the navigation page of the work
            // Output: the work itself
//            val urlString = "https://archiveofourown.org" + workRequest.queryString
//            println(urlString)

            val workUrlString = "https://archiveofourown.org" + workRequest.getEntireWorkURL()
            val navigationIndexUrlString = "https://archiveofourown.org" +
                    workRequest.getNavigationIndexPageURL()

            try {
                val workResponseBody = Network.get(workUrlString)
                val navigationIndexResponseBody = Network.get(navigationIndexUrlString)
                return HTMLParser.parseWorkAlternate(
                        workHtml = workResponseBody,
                        navigationHtml = navigationIndexResponseBody,
                        workURL = workRequest.url
                )
            } catch (e: Network.NetworkException) {
                throw e
            }
        }

        /* SEARCH FUNCTIONS */
        // FIXME: autocomplete results dont fetch unless a prior request was fired off from the actual site
        fun getAutocompleteResults(autocompleteRequest: AutocompleteRequest) : List<String> {
            val urlString = "https://archiveofourown.org/autocomplete" + autocompleteRequest.queryString
            println(urlString)

            val responseBody: String = try {
                Network.get(urlString)
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

    }

    private class AutocompleteResult(
        val id: String,
        val name: String
    )
}