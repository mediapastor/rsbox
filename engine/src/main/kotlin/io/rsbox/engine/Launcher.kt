package io.rsbox.engine

/**
 * @author Kyle Escobar
 */

object Launcher {

    lateinit var server: RSServer

    @JvmStatic
    fun main(args: Array<String>) {
        RSServer.logger.info { "Starting up RSBox." }
        server = RSServer(
            filestorePath = "rsbox/data/cache",
            args = args
        )
        server.init()
    }
}