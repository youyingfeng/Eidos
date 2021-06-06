package org.eidos.reader.container

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import org.eidos.reader.Database
import org.eidos.reader.SavedWork
import org.eidos.reader.network.Network
import org.eidos.reader.remote.AO3
import org.eidos.reader.remote.parser.HTMLParser
import org.eidos.reader.repository.EidosRepository
import org.eidos.reader.storage.Storage

class AppContainer(context: Context) {
    private val network = Network()
    private val parser = HTMLParser()
    private val remoteDataSource = AO3(network, parser)

    // the db name is called test.db
    private val driver: SqlDriver = AndroidSqliteDriver(Database.Schema, context, "test.db")
    private val database = Database(
        driver = driver,
        SavedWorkAdapter = SavedWork.Adapter(
            authorsAdapter = Storage.listOfStringsAdapter,
            gifteesAdapter = Storage.listOfStringsAdapter,
            fandomsAdapter = Storage.listOfStringsAdapter,
            warningsAdapter = Storage.listOfStringsAdapter,
            categoriesAdapter = Storage.listOfStringsAdapter,
            charactersAdapter = Storage.listOfStringsAdapter,
            relationshipsAdapter = Storage.listOfStringsAdapter,
            freeformsAdapter = Storage.listOfStringsAdapter,
            chaptersAdapter = Storage.chaptersAdapter
        )
    )
    private val localDataSource = Storage(database)

    val repository = EidosRepository(
        remoteDataSource = remoteDataSource,
        localDataSource = localDataSource
    )
}