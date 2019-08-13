package io.rsbox.server.net.packet.impl.message

import io.rsbox.server.net.packet.Message
import io.rsbox.server.net.packet.builder.*
import io.rsbox.server.net.packet.impl.encoder.InterfaceOpenTopEncoder

/**
 * @author Kyle Escobar
 */

class InterfaceOpenTopMessage(val top: Int) : Message {
    companion object: PacketDef {
        override val direction = PacketDirection.EGRESS
        override val coder = InterfaceOpenTopEncoder::class.java
        override val handler: Class<*>? = null
        override val structure = packet {
            message {
                opcode = 84
                type = PacketType.FIXED
            }
            frames(
                {
                    name = "top"
                    order = DataOrder.LITTLE
                    type = DataType.SHORT
                    trans = DataTransformation.ADD
                }
            )
        }
    }
}