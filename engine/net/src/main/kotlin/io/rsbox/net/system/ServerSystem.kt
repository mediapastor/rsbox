package io.rsbox.net.system

import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext

/**
 * @author Kyle Escobar
 */

abstract class ServerSystem(open val channel: Channel) {
    abstract fun recieveMessage(ctx: ChannelHandlerContext, msg: Any)

    abstract fun terminate()
}