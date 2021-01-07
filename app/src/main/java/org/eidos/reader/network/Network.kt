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
//                .addHeader("Referer", "https://archiveofourown.org/tags/Riza%20Hawkeye*s*Roy%20Mustang/works")
//                .addHeader("Cookie", "_otwarchive_session=Smt1NVMzbHJiT0thYnZXWWthTno2RXJoUXdydFU2TGEvMGpBaGhBenJDbEFOSFNRMGEwVVBnN09icnZrcUlvMW5DUFUxRmRUckVEN2VVSHFpZm9nT1VBWmlab2pPSkdGNXdZaGpUa0dJV0tRQ0hSVFFUMVBZQ1Z2MnZWTUhBREt4Z2ZDY3ozM3RIRHB3alJqV3lTdnpQS0tiRGdOWVNYNzFpNGxZZ0UvTVA4ZWFvbE85S3UvSi8yMDZzNlhmcUg2VFV0WktqaW5CeVNvSGh3TzZCVFdUR3pldWM0amk4U3AvM21TcTl5dzRKLzFibXA4N3hLYXdrYjNzdnJBdUdxQkg3UXpJcHZsMTBDZFJSVFg5MElmYUtjN3djSUQzbUdCNHBJdjVwLzcranY5ZzRMVnErdUNQVGNzcjcrazM4UEhhNkdneVB2d1RPS0tiOXN3MnJxU1F4WnBFVFREWmpZY3dmL0VSUFlXTUJsNEpmRnF4Ly9YTDdnRCtYV1RrK29HKzgzbXh2eVB1MC9zYmMwT3NpWUxlQT09LS1ZanRBVHZhNzZBVjBDWnM2empVeHpBPT0%3D--cb20911a7627db0900263fa36d9174f444614216; remember_user_token=BAhbCFsGaQKWsUkiIjhhNjczMzBlZGU4ZDJhYzUxMzI5Zjc1ZWU0MmNjBjoGRVRJIhcxNjA4NDAzNTA5LjU5Nzk4NjIGOwBG--93be5a166d7c9b7b34ad730a067854e7cf97941c; user_credentials=1; view_adult=true")
//                .addHeader("X-CSRF-Token", "zmjMwfm0E7mHN7bHotRKPrrcVpn/8+tCCK1DyRb6A7HC2G4w09elhuZYZk8oJ2HLS3NPB1Gf6tiWc7GvcUETXQ==")
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