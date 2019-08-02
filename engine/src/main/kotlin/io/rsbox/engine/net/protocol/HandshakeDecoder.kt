package io.rsbox.engine.net.protocol

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import io.rsbox.engine.net.codec.filestore.FilestoreDecoder
import io.rsbox.engine.net.codec.filestore.FilestoreEncoder
import io.rsbox.engine.net.codec.handshake.HandshakeMessage
import io.rsbox.engine.net.codec.handshake.HandshakeType
import io.rsbox.engine.net.codec.login.LoginDecoder
import io.rsbox.engine.net.codec.login.LoginEncoder
import mu.KotlinLogging
import java.math.BigInteger

/**
 * @author Kyle Escobar
 */

class HandshakeDecoder(
    private val revision: Int,
    private val cacheCrcs: IntArray,
    private val rsaExponent: BigInteger,
    private val rsModulus: BigInteger
) : ByteToMessageDecoder() {

    override fun decode(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>) {
        if(!buf.isReadable) {
            return
        }

        val opcode = buf.readByte().toInt()
        val handshake = HandshakeType.values.firstOrNull { it.id == opcode }

        when(handshake) {
            HandshakeType.JS5 -> {
                val p = ctx.pipeline()
                p.addFirst("filestore_encoder", FilestoreEncoder())
                p.addAfter("handshake_decoder", "filestore_decoder", FilestoreDecoder(revision))
            }

            HandshakeType.LOGIN -> {
                val p = ctx.pipeline()
                val seed = (Math.random() * Long.MAX_VALUE).toLong()

                p.addFirst("login_encoder", LoginEncoder())
                p.addAfter("handshake_decoder", "login_decoder", LoginDecoder(revision, cacheCrcs, seed, rsaExponent, rsModulus))

                ctx.writeAndFlush(ctx.alloc().buffer(1).writeByte(0))
                ctx.writeAndFlush(ctx.alloc().buffer(8).writeLong(seed))
            }

            else -> {
                buf.readBytes(buf.readableBytes())
                logger.warn("Unhandled handshake type {} requested by {}.", opcode, ctx.channel())
                return
            }
        }

        ctx.pipeline().remove(this)
        out.add(HandshakeMessage(handshake.id))
    }

    companion object {
        val logger = KotlinLogging.logger {}
    }
}