package io.rsbox.server.net.packet.impl.encoder

import io.rsbox.server.net.packet.MessageEncoder
import io.rsbox.server.net.packet.impl.message.InterfaceOpenTopMessage

/**
 * @author Kyle Escobar
 */

class InterfaceOpenTopEncoder : MessageEncoder<InterfaceOpenTopMessage>() {
    override fun extract(message: InterfaceOpenTopMessage, key: String): Number = when(key) {
        "top" -> message.top
        else -> throw Exception("Unhandled value key.")
    }

    override fun extractBytes(message: InterfaceOpenTopMessage, key: String): ByteArray = throw Exception("Unhandled value key.")
}