package io.rsbox.server.net.protocol.impl

import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.rsbox.server.net.protocol.GameProtocol

/**
 * @author Kyle Escobar
 */

class LoginProtocol(channel: Channel) : GameProtocol(channel) {
    override fun receiveMessage(ctx: ChannelHandlerContext, msg: Any) {

    }

    override fun terminate() {

    }
}