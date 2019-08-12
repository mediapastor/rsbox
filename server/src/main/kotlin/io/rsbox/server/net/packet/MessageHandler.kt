package io.rsbox.server.net.packet

import io.rsbox.server.model.world.World
import io.rsbox.server.model.entity.Client

/**
 * @author Kyle Escobar
 */

interface MessageHandler<T : Message> {
    fun handle(client: Client, world: World, message: T)
}