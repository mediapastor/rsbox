package io.rsbox.server.net.handshake

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import io.rsbox.server.net.js5.JS5Decoder
import io.rsbox.server.net.js5.JS5Encoder
import io.rsbox.server.net.login.LoginDecoder
import io.rsbox.server.net.login.LoginEncoder

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

            14 -> {
                val p = ctx.pipeline()
                val serverSeed = (Math.random() * Long.MAX_VALUE).toLong()

                p.addFirst("login_encoder", LoginEncoder())
                p.addAfter("handshake_decoder", "login_decoder", LoginDecoder(serverSeed))

                ctx.writeAndFlush(ctx.alloc().buffer(1).writeByte(0))
                ctx.writeAndFlush(ctx.alloc().buffer(8).writeLong(serverSeed))
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