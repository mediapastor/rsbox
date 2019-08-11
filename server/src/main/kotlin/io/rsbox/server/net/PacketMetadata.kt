package io.rsbox.server.net

import io.rsbox.server.net.packet.MessageStructureSet
import io.rsbox.server.net.packet.builder.IPacketMetadata
import io.rsbox.server.net.packet.builder.PacketType
import mu.KLogging

/**
 * @author Kyle Escobar
 */

class PacketMetadata(private val structures: MessageStructureSet) : IPacketMetadata {
    override fun getType(opcode: Int): PacketType? = structures.get(opcode)?.type

    override fun getLength(opcode: Int): Int {
        val structure = structures.get(opcode)
        if(structure == null) {
            logger.warn { "No message structure found for message with opcode $opcode." }
            return 0
        }
        return structure.length
    }

    override fun shouldIgnore(opcode: Int): Boolean {
        val structure = structures.get(opcode)
        if(structure == null) {
            logger.warn { "No message structure found for message with opcode $opcode." }
            return true
        }
        return structure.ignore
    }

    companion object : KLogging()
}