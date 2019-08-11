package io.rsbox.server.net.protocol.impl

import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.rsbox.server.net.protocol.ServerProtocol

/**
 * @author Kyle Escobar
 */

class GameProtocol(channel: Channel) : ServerProtocol(channel) {
    override fun receiveMessage(ctx: ChannelHandlerContext, msg: Any) {

    }

    override fun terminate() {

    }
}