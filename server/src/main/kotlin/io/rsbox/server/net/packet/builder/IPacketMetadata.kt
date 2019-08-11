package io.rsbox.server.net.packet.builder

/**
 * @author Kyle Escobar
 */

/**
 * @author Tom <rspsmods@gmail.com>
 */
interface IPacketMetadata {

    fun getType(opcode: Int): PacketType?

    fun getLength(opcode: Int): Int

    fun shouldIgnore(opcode: Int): Boolean
}