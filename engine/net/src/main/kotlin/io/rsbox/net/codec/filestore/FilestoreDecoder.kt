package io.rsbox.net.codec.filestore

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.rsbox.api.RSBox
import io.rsbox.net.codec.StatefulFrameDecoder
import io.rsbox.net.codec.login.LoginResultType
import mu.KotlinLogging

/**
 * @author Kyle Escobar
 */

class FilestoreDecoder(private val revision: Int) : StatefulFrameDecoder<FilestoreDecoderState>(FilestoreDecoderState.REVISION_REQUEST) {
    override fun decode(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>, state: FilestoreDecoderState) {
        when(state) {
            FilestoreDecoderState.REVISION_REQUEST -> decodeRevisionRequest(ctx, buf)
            FilestoreDecoderState.ARCHIVE_REQUEST -> decodeArchiveRequest(buf, out)
        }
    }

    private fun decodeRevisionRequest(ctx: ChannelHandlerContext, buf: ByteBuf) {
        if(buf.readableBytes() >= 4) {
            val revision = buf.readInt()
            if(revision != RSBox.server.config["server.revision"]) {
                logger.info("Revision mismatch for channel {} with client revision {}. Client must be using a revision {} gamepack to request.", ctx.channel(), revision, RSBox.server.config["server.revision"])
                ctx.writeAndFlush(LoginResultType.REVISION_MISMATCH).addListener(ChannelFutureListener.CLOSE)
            } else {
                setState(FilestoreDecoderState.ARCHIVE_REQUEST)

                ctx.writeAndFlush(LoginResultType.ACCEPTABLE)
            }
        }
    }

    private fun decodeArchiveRequest(buf: ByteBuf, out: MutableList<Any>) {
        if(!buf.isReadable) {
            return
        }

        buf.markReaderIndex()
        val opcode = buf.readByte().toInt()
        when(opcode) {
            CLIENT_INIT_GAME, CLIENT_LOAD_SCREEN, CLIENT_INIT_OPCODE -> {
                buf.skipBytes(3)
            }

            ARCHIVE_REQUEST_NEUTRAL, ARCHIVE_REQUEST_URGENT -> {
                if(buf.readableBytes() >= 3) {
                    val index = buf.readUnsignedByte().toInt()
                    val archive = buf.readUnsignedShort()

                    val request = FilestoreRequest(index, archive, opcode == ARCHIVE_REQUEST_URGENT)
                    out.add(request)
                } else {
                    buf.resetReaderIndex()
                }
            }

            else -> {
                logger.error { "Unhandled opcode: $opcode" }
            }
        }
    }

    companion object {
        val logger = KotlinLogging.logger {}

        private const val ARCHIVE_REQUEST_URGENT = 0
        private const val ARCHIVE_REQUEST_NEUTRAL = 1
        private const val CLIENT_INIT_GAME = 2
        private const val CLIENT_LOAD_SCREEN = 3
        private const val CLIENT_INIT_OPCODE = 6
    }
}