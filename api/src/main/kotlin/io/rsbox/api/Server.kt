package io.rsbox.api

import io.rsbox.api.world.World

/**
 * The base object which represents the engine server class.
 */
interface Server {
    var world: World
}