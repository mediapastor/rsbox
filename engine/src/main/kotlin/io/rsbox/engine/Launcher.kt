package io.rsbox.engine

/**
 * @author Kyle Escobar
 */

object Launcher {

    lateinit var server: Server

    @JvmStatic
    fun main(args: Array<String>) {
        Server.logger.info { "Starting up RSBox." }

        server = Server(
            filestorePath = "rsbox/data/cache",
            args = args
        )
        server.init()
    }
}