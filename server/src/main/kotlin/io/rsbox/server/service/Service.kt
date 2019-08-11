package io.rsbox.server.service

/**
 * @author Kyle Escobar
 */

abstract class Service {
    var loaded: Boolean = false

    abstract fun onStart()

    abstract fun onStop()
}