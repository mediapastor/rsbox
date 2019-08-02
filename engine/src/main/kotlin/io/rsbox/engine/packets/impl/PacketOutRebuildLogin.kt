package io.rsbox.engine.packets.impl

import io.rsbox.engine.model.world.Tile
import io.rsbox.engine.packets.PacketDef
import io.rsbox.engine.packets.PacketDirection
import io.rsbox.engine.net.packet.DataOrder
import io.rsbox.engine.net.packet.DataTransformation
import io.rsbox.engine.net.packet.DataType
import io.rsbox.engine.net.packet.Packet
import io.rsbox.engine.packets.packet
import io.rsbox.engine.service.impl.XteaKeyService
import io.rsbox.engine.net.packet.PacketType

/**
 * @author Kyle Escobar
 */

class PacketOutRebuildLogin(val playerIndex: Int, val tile: Tile, val playerTiles: IntArray, val xteaKeyService: XteaKeyService) :
    Packet {
    companion object : PacketDef {
        override val def =
                packet(PacketDirection.OUT) {
                    message {
                        type = PacketType.VARIABLE_SHORT
                        opcode = 0
                    }

                    structure(
                        {
                            name = "gpi"
                            type = DataType.BYTES
                        },
                        {
                            name = "z"
                            type = DataType.SHORT
                            order = DataOrder.LITTLE
                            transformation = DataTransformation.ADD
                        },
                        {
                            name = "x"
                            type = DataType.SHORT
                            transformation = DataTransformation.ADD
                        },
                        {
                            name = "xteas"
                            type = DataType.BYTES
                        }
                    )
                }
    }
}