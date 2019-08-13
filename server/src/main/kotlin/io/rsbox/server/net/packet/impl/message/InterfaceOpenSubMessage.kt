package io.rsbox.server.net.packet.impl.message

import io.rsbox.server.net.packet.Message
import io.rsbox.server.net.packet.builder.*
import io.rsbox.server.net.packet.impl.encoder.InterfaceOpenSubEncoder

/**
 * @author Kyle Escobar
 */

class InterfaceOpenSubMessage(val parent: Int, val child: Int, val component: Int, val type: Int): Message {
    companion object : PacketDef {
        override val direction = PacketDirection.EGRESS
        override val coder = InterfaceOpenSubEncoder::class.java
        override val handler: Class<*>? = null
        override val structure = packet {
            message {
                opcode = 77
                type = PacketType.FIXED
            }
            frames(
                {
                    name = "type"
                    type = DataType.BYTE
                    trans = DataTransformation.ADD
                },
                {
                    name = "overlay"
                    type = DataType.INT
                    order = DataOrder.MIDDLE
                },
                {
                    name = "component"
                    type = DataType.SHORT
                    order = DataOrder.LITTLE
                    trans = DataTransformation.ADD
                }
            )
        }
    }
}