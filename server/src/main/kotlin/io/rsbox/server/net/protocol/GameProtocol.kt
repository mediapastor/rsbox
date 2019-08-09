package io.rsbox.server.net.protocol

import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext

/**
 * @author Kyle Escobar
 */

abstract class GameProtocol(open val channel: Channel) {
    abstract fun receiveMessage(ctx: ChannelHandlerContext, msg: Any)

    abstract fun terminate()
}