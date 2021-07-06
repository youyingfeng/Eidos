package org.eidos.reader.container

import android.content.Context
import android.os.Build.VERSION.SDK_INT
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.work.WorkManager
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.util.CoilUtils
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import okhttp3.OkHttpClient
import org.eidos.reader.Database
import org.eidos.reader.network.Network
import org.eidos.reader.remote.AO3
import org.eidos.reader.remote.parser.HTMLParser
import org.eidos.reader.repository.EidosRepository
import org.eidos.reader.storage.Storage

class AppContainer
    constructor(
        applicationContext: Context,
        preferencesDataStore: DataStore<Preferences>,
        networkDataStore: DataStore<Preferences>
    )
{
    private val httpClient = OkHttpClient.Builder()
        .followRedirects(false)
        .cache(CoilUtils.createDefaultCache(applicationContext))     // FIXME: this line crashes if appcontainer is not lazy
        .build()
    private val network = Network(httpClient)
    private val parser = HTMLParser()
    private val remoteDataSource = AO3(network, parser)

    // the db name is called test.db
    private val driver: SqlDriver = AndroidSqliteDriver(Database.Schema, applicationContext, "test.db")
    private val database = Database(
        driver = driver,
        SavedWorkAdapter = Storage.savedWorkAdapter,
        ReadingHistoryWorkBlurbAdapter = Storage.readingHistoryWorkBlurbAdapter,
        ReadingListWorkBlurbAdapter = Storage.readingListWorkBlurbAdapter
    )
    private val localDataSource = Storage(database)
    private val workManager = WorkManager.getInstance(applicationContext)

    val repository = EidosRepository(
        remoteDataSource = remoteDataSource,
        localDataSource = localDataSource,
        preferencesDataStore = preferencesDataStore,
        workManager = workManager
    )

    val imageLoader = ImageLoader.Builder(applicationContext)
        .okHttpClient(httpClient)
        .componentRegistry {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder(applicationContext))
            } else {
                add(GifDecoder())
            }
        }
        .build()
    
}
