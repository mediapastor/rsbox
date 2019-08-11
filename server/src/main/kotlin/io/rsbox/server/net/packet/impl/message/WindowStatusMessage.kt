package io.rsbox.server.net.packet.impl.message

import io.rsbox.server.net.packet.Message
import io.rsbox.server.net.packet.builder.*
import io.rsbox.server.net.packet.impl.decoder.WindowStatusDecoder
import io.rsbox.server.net.packet.impl.handler.WindowStatusHandler

/**
 * @author Kyle Escobar
 */

class WindowStatusMessage(val mode: Int, val width: Int, val height: Int) : Message {
    companion object : PacketDef {
        override val direction = PacketDirection.INGRESS
        override val coder = WindowStatusDecoder::class.java
        override val handler = WindowStatusHandler::class.java
        override val structure = packet {
            message {
                opcode = 35
                length = 5
                type = PacketType.FIXED
            }

            frames(
                {
                    name = "mode"
                    type = DataType.BYTE
                },
                {
                    name = "width"
                    type = DataType.SHORT
                    sign = DataSignature.UNSIGNED
                },
                {
                    name = "height"
                    type = DataType.SHORT
                    sign = DataSignature.UNSIGNED
                }
            )
        }
    }
}