package io.rsbox.engine.net.protocol

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import io.rsbox.engine.net.codec.login.LoginResultType

/**
 * @author Kyle Escobar
 */

class HandshakeEncoder : MessageToByteEncoder<LoginResultType>() {
    override fun encode(ctx: ChannelHandlerContext, msg: LoginResultType, out: ByteBuf) {
        out.writeByte(msg.id)
    }
}