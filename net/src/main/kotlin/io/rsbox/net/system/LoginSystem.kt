package io.rsbox.net.system

import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.rsbox.api.RSBox
import io.rsbox.api.net.login.LoginRequest
import mu.KotlinLogging

/**
 * @author Kyle Escobar
 */

class LoginSystem(channel: Channel) : ServerSystem(channel) {

    override fun recieveMessage(ctx: ChannelHandlerContext, msg: Any) {
        if(msg is LoginRequest) {
            RSBox.server.queueLoginRequest(msg)
        }
    }

    override fun terminate() {
    }

    companion object {
        val logger = KotlinLogging.logger {}
    }
}