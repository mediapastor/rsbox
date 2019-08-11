package io.rsbox.api

import mu.KLogger

/**
 * The base object which represents the engine server class.
 */
interface Server {
    /**
     * Instance of KLogger to be used for logging messages to the console
     * Use this instead of println as it will keep the proper format.
     */
    val logger: KLogger

    var world: World
}