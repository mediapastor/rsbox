package io.rsbox.engine.net.codec.game

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import io.rsbox.engine.net.packet.GamePacket
import io.rsbox.engine.net.packet.PacketType
import io.rsbox.util.IsaacRandom
import mu.KotlinLogging

/**
 * @author Kyle Escobar
 */

class GamePacketEncoder(private val random: IsaacRandom) : MessageToByteEncoder<GamePacket>() {

    override fun encode(ctx: ChannelHandlerContext, msg: GamePacket, out: ByteBuf) {

        if(msg.type == PacketType.VARIABLE_BYTE && msg.length >= 256) {
            logger.error("Packet length ${msg.length} too long for 'variable-byte' packet on channel ${ctx.channel()}.")
            return
        }

        if(msg.type == PacketType.VARIABLE_SHORT && msg.length >= 65536) {
            logger.error("Packet length ${msg.length} too long for 'variable-short' packet on channel ${ctx.channel()}.")
            return
        }

        out.writeByte((msg.opcode + (random.nextInt())) and 0xFF)
        when(msg.type) {
            PacketType.VARIABLE_BYTE -> out.writeByte(msg.length)
            PacketType.VARIABLE_SHORT -> out.writeShort(msg.length)
            else -> {}
        }

        out.writeBytes(msg.payload)
        msg.payload.release()
    }

    companion object {
        val logger = KotlinLogging.logger {}
    }
}