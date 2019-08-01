package io.rsbox.engine.packets.impl

import io.rsbox.api.net.packet.DataType
import io.rsbox.api.net.packet.Packet
import io.rsbox.api.net.packet.PacketType
import io.rsbox.engine.packets.PacketDef
import io.rsbox.engine.packets.PacketDirection
import io.rsbox.engine.packets.packet

/**
 * @author Kyle Escobar
 */

class PacketOutRebootTimer(val cycles: Int) : Packet {

    companion object : PacketDef {
        override val def =
            packet(PacketDirection.OUT) {
                message {
                    type = PacketType.FIXED
                    opcode = 72
                }

                structure(
                    {
                        name = "cycles"
                        type = DataType.SHORT
                    }
                )
            }
    }

}