package org.eidos.reader.container

import org.eidos.reader.network.Network
import org.eidos.reader.remote.AO3
import org.eidos.reader.remote.parser.HTMLParser
import org.eidos.reader.repository.EidosRepository

class AppContainer {
    private val network = Network()
    private val parser = HTMLParser()
    private val remoteDataSource = AO3(network, parser)
    val repository = EidosRepository(remoteDataSource)
}