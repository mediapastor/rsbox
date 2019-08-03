package io.rsbox.engine.packets.impl

import io.rsbox.engine.net.packet.*
import io.rsbox.engine.packets.PacketDef
import io.rsbox.engine.packets.PacketDirection
import io.rsbox.engine.packets.packet
import io.rsbox.engine.service.impl.XteaKeyService

/**
 * @author Kyle Escobar
 */

class PacketOutRebuildRegion(val x: Int, val z: Int, val forceLoad: Int, val coordinates: IntArray, val xteaKeyService: XteaKeyService) : Packet {
    companion object : PacketDef {
        override val def =
                packet(PacketDirection.OUT) {
                    message {
                        type = PacketType.VARIABLE_SHORT
                        opcode = 51
                    }

                    structure(
                        {
                            name = "z"
                            type = DataType.SHORT
                            transformation = DataTransformation.ADD
                        },
                        {
                            name = "x"
                            type = DataType.SHORT
                            order = DataOrder.LITTLE
                            transformation = DataTransformation.ADD
                        },
                        {
                            name = "force_load"
                            type = DataType.BYTE
                        },
                        {
                            name = "data"
                            type = DataType.BYTES
                        }
                    )
                }
    }
}