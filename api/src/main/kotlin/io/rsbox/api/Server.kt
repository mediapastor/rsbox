package io.rsbox.api

import com.uchuhimo.konf.Config
import io.rsbox.api.net.login.LoginRequest
import mu.KLogger

/**
 * Represents the base level Server.
 * This is the lowest level of the running server and holds the access instance
 * to the engine logic.
 *
 * @author Kyle Escobar
 */
interface Server {
    /**
     * The object which can make calls to all loaded configs both from the
     * engine as well as any loaded plugins.
     */
    var config: Config

    /**
     * Used to add a auth request to the auth queue. Generally best
     * not to mess with this as it can eaisly break logging into your
     * server.
     *
     * @param request [LoginRequest] object passed from the network
     */
    fun queueLoginRequest(request: LoginRequest)

    /**
     * The object [World] which represents all interactions with the
     * game environment. Instance cached on the Server object.
     */
    var world: World

    /**
     * The instance of the logger from the server engine instance.
     * This can be used to log messages to the console.
     */
    var logger: KLogger
}