package io.rsbox.server.net.js5

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.rsbox.server.Launcher
import io.rsbox.server.net.ServerResultType
import io.rsbox.server.net.StatefulFrameDecoder
import mu.KLogging

/**
 * @author Kyle Escobar
 */

class JS5Decoder : StatefulFrameDecoder<JS5DecoderState>(JS5DecoderState.REVISION) {
    override fun decode(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>, state: JS5DecoderState) {
        when(state) {
            JS5DecoderState.REVISION -> decodeRevision(ctx,buf)
            JS5DecoderState.ARCHIVE -> decodeArchive(buf,out)
        }
    }

    private fun decodeRevision(ctx: ChannelHandlerContext, buf: ByteBuf) {
        if(buf.readableBytes() >= 4) {
            val revision = buf.readInt()
            if(revision != Launcher.server.revision) {
                logger.info { "Client connection for channel ${ctx.channel()} rejected due to an outdated client." }
                ctx.writeAndFlush(ServerResultType.REVISION_MISMATCH).addListener(ChannelFutureListener.CLOSE)
            } else {
                setState(JS5DecoderState.ARCHIVE)
                ctx.writeAndFlush(ServerResultType.ACCEPTABLE)
            }
        }
    }

    private fun decodeArchive(buf: ByteBuf, out: MutableList<Any>) {
        if(!buf.isReadable) {
            return
        }

        buf.markReaderIndex()
        val opcode = buf.readByte().toInt()
        /**
         * Opcode Chart
         * 0 = PRIORITY ARCHIVE REQUEST
         * 1 = NEUTRAL ARCHIVE REQUEST
         * 2 = TELL CLIENT TO INIT GAME
         * 3 = TELL CLIENT TO LOAD SCREEN
         * 6 = IDK BUT ITS NOT IMPORTANT AFAIK
         */
        when(opcode) {
            2,3,6 -> {
                buf.skipBytes(3)
            }

            0,1 -> {
                if(buf.readableBytes() >= 3) {
                    val index = buf.readUnsignedByte().toInt()
                    val archive = buf.readUnsignedShort()

                    val request = JS5Request(index, archive, (opcode == 0))
                    out.add(request)
                } else {
                    buf.resetReaderIndex()
                }
            }
            else -> {
                logger.error { "Unhandled opcode: $opcode."}
            }
        }
    }

    companion object : KLogging()
}

enum class JS5DecoderState {
    REVISION,
    ARCHIVE
}