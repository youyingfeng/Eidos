package org.eidos.reader.network

import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.Exception

class Network(val httpClient: OkHttpClient) {
    // TODO: In the future, this class will also need to handle cookies. But that is in the distant future.


    /*
    Input: URL
    Output: body of the resultant HTTP response
    Throws exception if body is empty / 404 / whatever
     */
    fun get(urlString: String) : String {
        val request: Request = Request.Builder()
            .url(urlString)
            .build()

        val responseBody : String = httpClient.newCall(request).execute()
            .body
            ?.string()
            ?: throw NetworkException("Empty response!")

        return responseBody
    }

    fun getJSON(urlString: String) : String {
        val request: Request = Request.Builder()
            .addHeader("X-Requested-With", "XMLHttpRequest")
            .addHeader("Accept", "application/json, text/javascript, */*; q=0.01")
            .url(urlString)
            .build()

        val responseBody : String = httpClient.newCall(request).execute()
            .body
            ?.string()
            ?: throw NetworkException("Empty response!")

        return responseBody
    }

    fun getJS(urlString: String) : String {
        val request: Request = Request.Builder()
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Accept", "*/*;q=0.5, text/javascript, application/javascript, application/ecmascript, application/x-ecmascript")
                .url(urlString)
                .build()

        val responseBody : String = httpClient.newCall(request).execute()
                .body
                ?.string()
                ?: throw NetworkException("Empty response!")

        return responseBody
    }

    // TODO: specify network exceptions - e.g. for 404
    class NetworkException(message: String) : Exception(message)
}