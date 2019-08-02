package io.rsbox.engine.packets

import io.rsbox.engine.net.packet.IPacketMetadata
import io.rsbox.engine.net.packet.PacketType
import mu.KotlinLogging

/**
 * @author Kyle Escobar
 */

class PacketMetadata(private val structures: PacketStructureSet) : IPacketMetadata {
    override fun getType(opcode: Int): PacketType? = structures.get(opcode)?.type

    override fun getLength(opcode: Int): Int {
        val structure = structures.get(opcode)
        if(structure == null) {
            logger.warn("No packet structure found for packet[opcode=${opcode}].")
            return 0
        }
        return structure.length
    }

    override fun shouldIgnore(opcode: Int): Boolean {
        val structure = structures.get(opcode)
        if(structure == null) {
            logger.warn("No packet structure found for packet[opcode=${opcode}].")
            return true
        }
        return structure.ignore
    }

    companion object {
        val logger = KotlinLogging.logger {}
    }
}