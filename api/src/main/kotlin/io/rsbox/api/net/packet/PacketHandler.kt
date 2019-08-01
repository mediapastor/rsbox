package io.rsbox.api.net.packet

import io.rsbox.api.World
import io.rsbox.api.entity.Client

/**
 * @author Kyle Escobar
 */

interface PacketHandler<T: Packet> {
    fun handle(client: Client, world: World, message: T)
}