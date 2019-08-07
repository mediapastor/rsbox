package io.rsbox.net

import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.util.AttributeKey
import io.rsbox.net.handshake.HandshakeMessage
import io.rsbox.net.protocol.GameProtocol
import io.rsbox.net.protocol.impl.JS5Protocol
import mu.KLogging

/**
 * @author Kyle Escobar
 */

@ChannelHandler.Sharable
class GameHandler : ChannelInboundHandlerAdapter() {

    override fun channelInactive(ctx: ChannelHandlerContext) {
        val session = ctx.channel().attr(PROTOCOL_KEY).andRemove
        if(session != null) {
            session.terminate()
            ctx.channel().close()
        }
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        val attribute = ctx.channel().attr(PROTOCOL_KEY)
        val protocol = attribute.get()
        if(protocol != null) {
            protocol.receiveMessage(ctx,msg)
        } else if(msg is HandshakeMessage) {
            when(msg.id) {
                14 -> attribute.set(JS5Protocol(ctx.channel()))
            }
        }
    }

    companion object : KLogging() {
        val PROTOCOL_KEY: AttributeKey<GameProtocol> = AttributeKey.valueOf("protocol")
    }
}