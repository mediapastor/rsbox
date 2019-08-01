package io.rsbox.api.net.packet

/**
 * @author Kyle Escobar
 */

interface IPacketMetadata {

    fun getType(opcode: Int): PacketType?

    fun getLength(opcode: Int): Int

    fun shouldIgnore(opcode: Int): Boolean
}