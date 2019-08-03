package io.rsbox.engine.net.packet

import io.rsbox.engine.model.entity.Client
import io.rsbox.engine.model.world.World

/**
 * @author Kyle Escobar
 */

interface PacketHandler<T: Packet> {
    fun handle(client: Client, world: World, message: T)
}