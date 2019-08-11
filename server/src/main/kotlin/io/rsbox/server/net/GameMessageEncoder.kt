package io.rsbox.server.net

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageEncoder
import io.rsbox.server.net.packet.Message
import io.rsbox.server.net.packet.MessageEncoderSet
import io.rsbox.server.net.packet.MessageStructureSet
import io.rsbox.server.net.packet.builder.GamePacketBuilder
import mu.KLogging

/**
 * @author Kyle Escobar
 */

class GameMessageEncoder(private val encoders: MessageEncoderSet, private val structures: MessageStructureSet) : MessageToMessageEncoder<Message>() {
    override fun encode(ctx: ChannelHandlerContext, msg: Message, out: MutableList<Any>) {
        val encoder = encoders.get(msg.javaClass)
        val structure = structures.get(msg.javaClass)

        if(encoder == null) {
            logger.error { "No encoder found for message $msg." }
            return
        }

        if(structure == null) {
            logger.error { "No packet structure found for message $msg." }
            return
        }

        val builder = GamePacketBuilder(structure.opcode, structure.type)
        encoder.encode(msg, builder, structure)
        out.add(builder.toGamePacket())
    }

    companion object : KLogging()
}