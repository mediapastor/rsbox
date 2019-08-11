package io.rsbox.server.net.packet

import io.rsbox.server.net.packet.builder.DataSignature
import io.rsbox.server.net.packet.builder.DataType
import io.rsbox.server.net.packet.builder.GamePacketReader

/**
 * @author Kyle Escobar
 */

abstract class MessageDecoder<T: Message> {
    open fun decode(opcode: Int, structure: MessageStructure, reader: GamePacketReader): T {
        val values = hashMapOf<String, Number>()
        val stringValues = hashMapOf<String, String>()
        structure.values.values.forEach { value ->
            when(value.type) {
                DataType.BYTES -> throw Exception("Cannot decode message with type ${value.type}.")
                DataType.STRING -> stringValues[value.id] = reader.string
                DataType.SMART -> {
                    if(value.sign == DataSignature.SIGNED) {
                        values[value.id] = reader.signedSmart
                    } else {
                        values[value.id] = reader.unsignedSmart
                    }
                }
                else -> {
                    if(value.sign == DataSignature.SIGNED) {
                        values[value.id] = reader.getSigned(value.type, value.order, value.trans)
                    } else {
                        values[value.id] = reader.getUnsigned(value.type, value.order, value.trans)
                    }
                }
            }
        }
        return decode(opcode, structure.opcode, values, stringValues)
    }

    abstract fun decode(opcode: Int, opcodeIndex: Int, values: HashMap<String, Number>, stringValues: HashMap<String, String>): T
}