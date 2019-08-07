package io.rsbox.net.handshake

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import io.rsbox.net.GameHandler
import io.rsbox.net.js5.JS5Decoder
import io.rsbox.net.js5.JS5Encoder

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
            15 -> {
                val p = ctx.pipeline()
                p.addFirst("js5_encoder", JS5Encoder())
                p.addAfter("handshake_decoder", "js5_decoder", JS5Decoder())
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