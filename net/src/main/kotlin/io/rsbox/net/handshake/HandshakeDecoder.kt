package io.rsbox.net.handshake

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import io.rsbox.net.GameHandler

/**
 * @author Kyle Escobar
 */

class HandshakeDecoder : ByteToMessageDecoder(){
    override fun decode(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>) {
        if(!buf.isReadable) {
            return
        }

        val opcode = buf.readByte().toInt()
        when(opcode) {
            14 -> {
                val p = ctx.pipeline()
                p.addFirst("cache_encoder", null)
                p.addAfter("handshake_decoder", "cache_decoder", null)
            }

            else -> {
                buf.readBytes(buf.readableBytes())
                return
            }
        }

        ctx.pipeline().remove(this)
        out.add(HandshakeMessage(opcode))
    }
}