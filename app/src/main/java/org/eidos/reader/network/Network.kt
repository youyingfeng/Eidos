package org.eidos.reader.network

import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.Exception

class Network {
    // TODO: In the future, this class will also need to handle cookies. But that is in the distant future.
    companion object {
        private val httpClient: OkHttpClient = OkHttpClient()

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
                .addHeader("Referer", "https://archiveofourown.org/works/search")
                .addHeader("Cookie", "_otwarchive_session=dGYwL3orNWFnbHc2djFlWnhnMDFCMmxxTUl3VFVZZWZKamNOWW9NdktUcFFUNVhZRlplajdUbkRUQUZ2dHluR1FjNXF0UXg0YVpnOVdmdEtoSklQRHp1SmRUY2FCRGtuMVNraTZiR3hXcXlJTiswdkpSaFV4NUxIZlkrVEYzd1lQbEpVd0IxRWUzUXJ6S1JEMC9hc3AxbHVRVEtVMWlpNHNYVGhzd1VTUmpOVGVUUUVXVkhxblZOMnpGWjBiaG4xMVFCaGU0bDhpQXV5UW5ydllyMVpZZTAzS0x3U3plN2VpV1M0TU9jTndUZmdIMWJ5Q3l5SmpwRnErQzJaNkZxN2NEeTJVL2FKSkRBci9BOUd1Sm9WZmdrMitjbzk2U3M1TXQ0bnVPRnRxbXFRNzNaMUJpVGxnRWRuV244ZEJyZnduWkR5WjFyeUlhSENGQkZWV0lKVjFFck5RSU0zMFk5eGJhU0pBT0RkaEo0PS0tenNQejBpc0VYNWQ1aDAyZjJ0R1pwUT09--5285f77f44c875803ed202257eadaaa96e04834f; remember_user_token=BAhbCFsGaQKWsUkiIjhhNjczMzBlZGU4ZDJhYzUxMzI5Zjc1ZWU0MmNjBjoGRVRJIhcxNjA4NDAzNTA5LjU5Nzk4NjIGOwBG--93be5a166d7c9b7b34ad730a067854e7cf97941c; user_credentials=1; view_adult=true")
                .addHeader("X-CSRF-Token", "o5BHjdzL8GkDedmxPVWAloTDHLIMrC5BD9mbLvEjjRevIOV89qhGVmIWCTm3pqtjdWwFLKLAL9uRB2lIlpid+w==")
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
    }

    // TODO: specify network exceptions - e.g. for 404
    class NetworkException(message: String) : Exception(message)
}