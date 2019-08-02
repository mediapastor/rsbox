package io.rsbox.engine.packets

import io.rsbox.api.net.packet.Packet
import io.rsbox.api.net.packet.PacketDecoder
import io.rsbox.api.net.packet.PacketHandler
import java.lang.RuntimeException

/**
 * @author Kyle Escobar
 */

class PacketDecoderSet {
    private val decoders = arrayOfNulls<PacketDecoder<*>>(256)

    private val handlers = arrayOfNulls<PacketHandler<out Packet>>(256)

    fun init(structures: PacketStructureSet) {

    }

    private fun <T: Packet> put(packetType: Class<T>, decoderType: PacketDecoder<T>, handlerType: PacketHandler<T>, structures: PacketStructureSet) {
        val structure = structures.get(packetType) ?: throw RuntimeException("Packet definition has not been defined  in the packet class companion object for packet=${packetType.simpleName}.")
        structure.opcodes.forEach { opcode ->
            decoders[opcode] = decoderType
            handlers[opcode] = handlerType
        }
    }

    fun get(opcode: Int): PacketDecoder<*>? {
        return decoders[opcode]
    }

    @Suppress("UNCHECKED_CAST")
    fun getHandler(opcode: Int): PacketHandler<Packet>? {
        return handlers[opcode] as PacketHandler<Packet>?
    }
}