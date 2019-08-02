package io.rsbox.engine.packets

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageEncoder
import io.rsbox.engine.net.packet.GamePacketBuilder
import io.rsbox.engine.net.packet.Packet
import mu.KotlinLogging

/**
 * @author Kyle Escobar
 */

class PacketBuilderEncoder(private val encoders: PacketEncoderSet, private val structures: PacketStructureSet) : MessageToMessageEncoder<Packet>() {

    override fun encode(ctx: ChannelHandlerContext, msg: Packet, out: MutableList<Any>) {
        val encoder = encoders.get(msg.javaClass)
        val structure = structures.get(msg.javaClass)

        if(encoder == null) {
            logger.error("No encoder found for packet $msg.")
            return
        }

        if(structure == null) {
            logger.error("No packet structure found for message $msg.")
            return
        }

        val builder = GamePacketBuilder(structure.opcodes.first(), structure.type)
        encoder.encode(msg, builder, structure)
        out.add(builder.toGamePacket())
    }


    companion object {
        val logger = KotlinLogging.logger {}
    }

}