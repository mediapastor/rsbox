package io.rsbox.engine.net.packet

/**
 * @author Kyle Escobar
 */

data class PacketHandle(val message: Packet, val handler: PacketHandler<Packet>, val opcode: Int, val length: Int)