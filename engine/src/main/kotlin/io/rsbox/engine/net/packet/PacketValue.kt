package io.rsbox.engine.net.packet

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