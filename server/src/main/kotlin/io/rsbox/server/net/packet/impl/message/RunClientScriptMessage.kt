package io.rsbox.server.net.packet.impl.message

import io.rsbox.server.net.packet.Message
import io.rsbox.server.net.packet.builder.*
import io.rsbox.server.net.packet.impl.encoder.RunClientScriptEncoder

/**
 * @author Kyle Escobar
 */

class RunClientScriptMessage(val id: Int, vararg val args: Any) : Message {
    companion object : PacketDef {
        override val direction = PacketDirection.EGRESS
        override val coder = RunClientScriptEncoder::class.java
        override val handler: Class<*>? = null
        override val structure = packet {
            message {
                opcode = 62
                type = PacketType.VARIABLE_SHORT
            }
            frames(
                {
                    name = "types"
                    type = DataType.BYTES
                },
                {
                    name = "args"
                    type = DataType.BYTES
                },
                {
                    name = "id"
                    type = DataType.INT
                }
            )
        }
    }
}