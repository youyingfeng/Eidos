package org.eidos.reader.network

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
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

        val response = try {
            httpClient.newCall(request).execute()
        } catch (e: IOException) {
            throw NetworkException()
        }

        when(response.code) {
            403, 404, 422, 500, 502 -> throw ServerException()
            302 -> {
                val location = response.headers["location"] ?: throw NotFoundException()
                throw RedirectException(locationUrl = location)
            }
        }

        val responseBody : String = response
            .body!!
            .string()

        response.close()

        return responseBody
    }

    fun getJSON(urlString: String) : String {
        val request: Request = Request.Builder()
            .addHeader("X-Requested-With", "XMLHttpRequest")
            .addHeader("Accept", "application/json, text/javascript, */*; q=0.01")
            .url(urlString)
            .build()

        val response = try {
            httpClient.newCall(request).execute()
        } catch (e: IOException) {
            throw NetworkException()
        }

        when(response.code) {
            403, 404, 422, 500, 502 -> throw ServerException()
            302 -> {
                val location = response.headers["location"] ?: throw NotFoundException()
                throw RedirectException(locationUrl = location)
            }
        }

        val responseBody : String = response
            .body!!
            .string()

        response.close()

        return responseBody
    }

//    fun getJS(urlString: String) : String {
//        val request: Request = Request.Builder()
//                .addHeader("X-Requested-With", "XMLHttpRequest")
//                .addHeader("Accept", "*/*;q=0.5, text/javascript, application/javascript, application/ecmascript, application/x-ecmascript")
//                .url(urlString)
//                .build()
//
//        val response = try {
//            httpClient.newCall(request).execute()
//        } catch (e: IOException) {
//            throw NetworkException()
//        }
//
//        when(response.code) {
//            403, 404, 422, 500, 502 -> throw ServerException()
//            302 -> {
//                val location = response.headers["location"] ?: throw NotFoundException()
//                throw RedirectException(locationUrl = location)
//            }
//        }
//
//        val responseBody : String = response
//            .body!!
//            .string()
//
//        response.close()
//
//        return responseBody
//    }

    // TODO: specify network exceptions - e.g. for 404
    // codes are 403 404 422 500 502, auth, and redirection
    // redirects are 302 unless specified
    // these are the base classes
    open class ServerException(message: String = "Could not find content on the Archive") : Exception(message)
    class RedirectException(message: String = "AO3 is redirecting you to another page", val locationUrl: String) : Exception(message)
    class AuthenticationException(message: String) : Exception(message)

    // these should either be deleted or extend one of the above
    class NetworkException(message: String = "There is a problem with the network connection") : Exception(message)
    class NotFoundException(message: String = "Could not find content on the Archive") : ServerException(message)


}