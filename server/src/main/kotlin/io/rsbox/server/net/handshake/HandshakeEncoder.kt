package io.rsbox.server.net.handshake

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import io.rsbox.api.ServerResultType

/**
 * @author Kyle Escobar
 */

class HandshakeEncoder : MessageToByteEncoder<ServerResultType>() {
    override fun encode(ctx: ChannelHandlerContext, msg: ServerResultType, out: ByteBuf) {
        out.writeByte(msg.id)
    }
}