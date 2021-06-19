package org.eidos.reader.network

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class EidosCookieJar(val networkDataStore: DataStore<Preferences>) : CookieJar {
    // FIXME: what about non-AO3 stuff?
    private val storage = mutableListOf<Cookie>()   // this might be a shit implementation
    // double hash map should work better? domain -> cookie name -> cookie

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        TODO("Not yet implemented")
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        TODO("Not yet implemented")
    }
}