package io.rsbox.server.net.game

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import io.rsbox.server.net.packet.builder.GamePacket
import io.rsbox.server.net.packet.builder.PacketType
import io.rsbox.server.util.IsaacRandom
import mu.KLogging

/**
 * @author Kyle Escobar
 */

class GamePacketEncoder(private val random: IsaacRandom) : MessageToByteEncoder<GamePacket>() {
    override fun encode(ctx: ChannelHandlerContext, msg: GamePacket, out: ByteBuf) {
        if(msg.type == PacketType.VARIABLE_BYTE && msg.length >= 256) {
            logger.error { "Message length of ${msg.length} is too long for 'variable-byte' packet type." }
            return
        }

        if(msg.type == PacketType.VARIABLE_SHORT && msg.length >= 65536) {
            logger.error { "Message length of ${msg.length} is too long for 'variable-short' packet type." }
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

    companion object : KLogging()
}