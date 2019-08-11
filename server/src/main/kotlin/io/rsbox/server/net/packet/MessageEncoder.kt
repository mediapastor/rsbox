package io.rsbox.server.net.packet

import io.rsbox.server.net.packet.builder.DataType
import io.rsbox.server.net.packet.builder.GamePacketBuilder

/**
 * @author Kyle Escobar
 */

abstract class MessageEncoder<T: Message> {
    fun encode(message: T, builder: GamePacketBuilder, structure: MessageStructure) {
        structure.values.values.forEach { value ->
            if(value.type != DataType.BYTES) {
                builder.put(value.type, value.order, value.trans, extract(message, value.id))
            } else {
                builder.putBytes(extractBytes(message, value.id))
            }
        }
    }

    abstract fun extract(message: T, key: String): Number

    abstract fun extractBytes(message: T, key: String): ByteArray
}