package io.rsbox.engine.service

import io.rsbox.api.Server
import mu.KotlinLogging

/**
 * @author Kyle Escobar
 */

abstract class Service {

    val isRunning: Boolean = false

    val logger = KotlinLogging.logger {}

    abstract fun onStart(server: Server)

    abstract fun onStop(server: Server)
}