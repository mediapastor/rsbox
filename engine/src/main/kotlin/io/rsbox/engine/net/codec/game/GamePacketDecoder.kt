package io.rsbox.engine.net.codec.game

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.rsbox.engine.net.codec.StatefulFrameDecoder
import io.rsbox.engine.net.packet.GamePacket
import io.rsbox.engine.net.packet.IPacketMetadata
import io.rsbox.engine.net.packet.PacketType
import io.rsbox.util.IsaacRandom
import mu.KotlinLogging

/**
 * @author Kyle Escobar
 */

class GamePacketDecoder(private val random: IsaacRandom, private val packetMetadata: IPacketMetadata) : StatefulFrameDecoder<GameDecoderState>(GameDecoderState.OPCODE) {

    private var opcode = 0

    private var length = 0

    private var type = PacketType.FIXED

    private var ignore = false

    override fun decode(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>, state: GameDecoderState) {
        when(state) {
            GameDecoderState.OPCODE -> decodeOpcode(ctx, buf, out)
            GameDecoderState.LENGTH -> decodeLength(buf, out)
            GameDecoderState.PAYLOAD -> decodePayload(buf, out)
        }
    }

    private fun decodeOpcode(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>) {
        if(buf.isReadable) {
            opcode = buf.readUnsignedByte().toInt() - (random.nextInt()) and 0xFF
            val packetType = packetMetadata.getType(opcode)
            if(packetType == null) {
                logger.warn("Channel ${ctx.channel()} sent packet[opcode=${opcode}] with no valid metadata.")
                buf.skipBytes(buf.readableBytes())
                return
            }

            type = packetType
            ignore = packetMetadata.shouldIgnore(opcode)

            when(type) {
                PacketType.FIXED -> {
                    length = packetMetadata.getLength(opcode)
                    if(length != 0) {
                        setState(GameDecoderState.PAYLOAD)
                    } else if(!ignore) {
                        out.add(GamePacket(opcode, type, Unpooled.EMPTY_BUFFER))
                    }
                }

                PacketType.VARIABLE_BYTE, PacketType.VARIABLE_SHORT -> setState(GameDecoderState.LENGTH)

                else -> throw IllegalStateException("Unhandled packet type $type with opcode $opcode.")
            }
        }
    }

    private fun decodeLength(buf: ByteBuf, out: MutableList<Any>) {
        if(buf.isReadable) {
            length = if(type == PacketType.VARIABLE_SHORT) buf.readUnsignedShort() else buf.readUnsignedByte().toInt()

            if(length != 0) {
                setState(GameDecoderState.PAYLOAD)
            } else if(!ignore) {
                out.add(GamePacket(opcode, type, Unpooled.EMPTY_BUFFER))
            }
        }
    }

    private fun decodePayload(buf: ByteBuf, out: MutableList<Any>) {
        if(buf.readableBytes() >= length) {
            val payload = buf.readBytes(length)
            setState(GameDecoderState.OPCODE)

            if(!ignore) {
                out.add(GamePacket(opcode, type, payload))
            }
        }
    }

    companion object {
        val logger = KotlinLogging.logger {}
    }
}