package io.rsbox.api.event

import io.rsbox.api.Server

/**
 * Executed when the server finishes starting up completely.
 * This event can be cancelled.
 *
 * @param server An instance of the server that has just been started.
 */
class ServerStartEvent(val server: Server) : Event(), Cancelable {

    override var cancelled: Boolean = false

    override fun handler(): Boolean = cancelled
}