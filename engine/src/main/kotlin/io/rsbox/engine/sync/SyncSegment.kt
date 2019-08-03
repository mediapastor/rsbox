package io.rsbox.engine.sync

import io.rsbox.engine.net.packet.GamePacketBuilder

/**
 * @author Kyle Escobar
 */

interface SyncSegment {
    fun encode(buf: GamePacketBuilder)
}