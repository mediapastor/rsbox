package io.rsbox.server.net.packet.impl.encoder

import io.rsbox.server.net.packet.MessageEncoder
import io.rsbox.server.net.packet.impl.message.InterfaceOpenSubMessage

/**
 * @author Kyle Escobar
 */

class InterfaceOpenSubEncoder : MessageEncoder<InterfaceOpenSubMessage>() {
    override fun extract(message: InterfaceOpenSubMessage, key: String): Number = when(key) {
        "component" -> message.component
        "overlay" -> (message.parent shl 16) or message.child
        "type" -> message.type
        else -> throw Exception("Unhandled value key.")
    }

    override fun extractBytes(message: InterfaceOpenSubMessage, key: String): ByteArray = throw Exception("Unhandled value key.")
}