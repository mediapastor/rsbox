package io.rsbox.engine.net.packet

import io.rsbox.engine.model.entity.Client
import io.rsbox.engine.model.world.RSWorld

/**
 * @author Kyle Escobar
 */

interface PacketHandler<T: Packet> {
    fun handle(client: Client, world: RSWorld, message: T)
}