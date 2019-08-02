package io.rsbox.engine.system.auth

import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.rsbox.engine.Launcher
import io.rsbox.engine.system.ServerSystem
import io.rsbox.engine.net.codec.login.LoginRequest
import mu.KotlinLogging

/**
 * @author Kyle Escobar
 */

class LoginSystem(channel: Channel) : ServerSystem(channel) {

    override fun recieveMessage(ctx: ChannelHandlerContext, msg: Any) {
        if(msg is LoginRequest) {
            Launcher.server.queueLoginRequest(msg)
        }
    }

    override fun terminate() {
    }

    companion object {
        val logger = KotlinLogging.logger {}
    }
}