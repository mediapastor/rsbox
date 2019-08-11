package io.rsbox.api

/**
 * This is the base object where the server hooks into the API
 *
 * @author Kyle Escobar
 */
object Api {
    /**
     * The instance of [Server] for static reference in plugins.
     */
    lateinit var server: Server

    /**
     * Called when the server starts up.
     * Handles initialization of various things in the API
     */
    fun init() {

        /**
         * Initializes the [EventManager]
         */
        EventManager.init()
    }
}