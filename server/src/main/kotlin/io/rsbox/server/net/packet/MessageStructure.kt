package io.rsbox.server.net.packet

import io.rsbox.server.net.packet.builder.PacketType
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap

/**
 * @author Kyle Escobar
 */

data class MessageStructure(
    val type: PacketType,
    val opcode: Int,
    val length: Int,
    val ignore: Boolean,
    val values: Object2ObjectLinkedOpenHashMap<String, MessageValue>
)