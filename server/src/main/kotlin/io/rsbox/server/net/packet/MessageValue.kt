package io.rsbox.server.net.packet

import io.rsbox.server.net.packet.builder.DataOrder
import io.rsbox.server.net.packet.builder.DataSignature
import io.rsbox.server.net.packet.builder.DataTransformation
import io.rsbox.server.net.packet.builder.DataType

/**
 * @author Kyle Escobar
 */

data class MessageValue(
    val id: String,
    val order: DataOrder,
    val trans: DataTransformation,
    val type: DataType,
    val sign: DataSignature
)