package io.rsbox.server.net.packet.impl.message

import io.rsbox.server.model.world.Tile
import io.rsbox.server.net.packet.Message
import io.rsbox.server.net.packet.builder.*
import io.rsbox.server.net.packet.impl.encoder.RebuildLoginEncoder
import io.rsbox.server.service.impl.XteaKeyService

/**
 * @author Kyle Escobar
 */

class RebuildLoginMessage(val playerIndex: Int, val tile: Tile, val playerTiles: IntArray, val xteaKeyService: XteaKeyService) : Message {
    companion object : PacketDef {
        override val direction = PacketDirection.EGRESS
        override val coder = RebuildLoginEncoder::class.java
        override val handler: Class<*>? = null
        override val structure = packet {
            message {
                opcode = 0
                type = PacketType.VARIABLE_SHORT
            }
            frames(
                {
                    name = "gpi"
                    type = DataType.BYTES
                },
                {
                    name = "z"
                    type = DataType.SHORT
                    order = DataOrder.LITTLE
                    trans = DataTransformation.ADD
                },
                {
                    name = "x"
                    type = DataType.SHORT
                    trans = DataTransformation.ADD
                },
                {
                    name = "xteas"
                    type = DataType.BYTES
                }
            )
        }
    }
}