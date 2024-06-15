package de.mastsev.deblock.flights.app.test

import java.net.ServerSocket

@Synchronized
fun randomPort() = ServerSocket(0).use { it.localPort }
