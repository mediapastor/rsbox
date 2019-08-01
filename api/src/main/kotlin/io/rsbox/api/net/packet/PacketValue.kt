package io.rsbox.api.net.packet

import io.rsbox.api.net.packet.DataOrder
import io.rsbox.api.net.packet.DataSignature
import io.rsbox.api.net.packet.DataTransformation
import io.rsbox.api.net.packet.DataType

/**
 * @author Kyle Escobar
 */

data class PacketValue(
    val id: String,
    val order: DataOrder,
    val transformation: DataTransformation,
    val type: DataType,
    val signature: DataSignature
)