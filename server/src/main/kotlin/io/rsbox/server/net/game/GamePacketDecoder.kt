package io.rsbox.server.net.game

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.rsbox.server.net.StatefulFrameDecoder
import io.rsbox.server.net.packet.builder.GamePacket
import io.rsbox.server.net.packet.builder.IPacketMetadata
import io.rsbox.server.net.packet.builder.PacketType
import io.rsbox.server.util.IsaacRandom
import mu.KLogging

/**
 * @author Kyle Escobar
 */

class GamePacketDecoder(private val random: IsaacRandom, private val packetMetadata: IPacketMetadata) : StatefulFrameDecoder<GamePacketDecoderState>(GamePacketDecoderState.OPCODE) {

    private var opcode = 0

    private var length = 0

    private var type = PacketType.FIXED

    private var ignore = false

    override fun decode(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>, state: GamePacketDecoderState) {
        when(state) {
            GamePacketDecoderState.OPCODE -> decodeOpcode(ctx,buf,out)
            GamePacketDecoderState.LENGTH -> decodeLength(buf,out)
            GamePacketDecoderState.PAYLOAD -> decodePayload(buf,out)
        }
    }

    private fun decodeOpcode(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>) {
        if(buf.isReadable) {
            opcode = buf.readUnsignedByte().toInt() - (random.nextInt()) and 0xFF
            val packetType = packetMetadata.getType(opcode)
            if(packetType == null) {
                logger.warn { "Channel ${ctx.channel()} sent a message with no valid metadata. Opcode = $opcode" }
                buf.skipBytes(buf.readableBytes())
                return
            }

            type = packetType
            ignore = packetMetadata.shouldIgnore(opcode)

            when(type) {
                PacketType.FIXED -> {
                    length = packetMetadata.getLength(opcode)
                    if(length != 0) {
                        setState(GamePacketDecoderState.PAYLOAD)
                    } else if(!ignore) {
                        out.add(GamePacket(opcode, type, Unpooled.EMPTY_BUFFER))
                    }
                }

                PacketType.VARIABLE_BYTE, PacketType.VARIABLE_SHORT -> setState(GamePacketDecoderState.LENGTH)
                else -> throw IllegalStateException("Unhandled packet type $type for opcode $opcode")
            }
        }
    }

    private fun decodeLength(buf: ByteBuf, out: MutableList<Any>) {
        if(buf.isReadable) {
            length = if(type == PacketType.VARIABLE_SHORT) buf.readUnsignedShort() else buf.readUnsignedByte().toInt()
            if(length != 0) {
                setState(GamePacketDecoderState.PAYLOAD)
            } else if(!ignore) {
                out.add(GamePacket(opcode,type,Unpooled.EMPTY_BUFFER))
            }
        }
    }

    private fun decodePayload(buf: ByteBuf, out: MutableList<Any>) {
        if(buf.readableBytes() >= length) {
            val payload = buf.readBytes(length)
            setState(GamePacketDecoderState.OPCODE)

            if(!ignore) {
                out.add(GamePacket(opcode, type, payload))
            }
        }
    }

    companion object : KLogging()
}

enum class GamePacketDecoderState {
    OPCODE,
    LENGTH,
    PAYLOAD
}