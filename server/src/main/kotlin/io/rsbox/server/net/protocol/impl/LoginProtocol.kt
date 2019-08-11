package io.rsbox.server.net.protocol.impl

import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.rsbox.server.net.login.LoginRequest
import io.rsbox.server.net.protocol.ServerProtocol
import io.rsbox.server.service.ServiceManager
import io.rsbox.server.service.impl.AuthService

/**
 * @author Kyle Escobar
 */

class LoginProtocol(channel: Channel) : ServerProtocol(channel) {
    override fun receiveMessage(ctx: ChannelHandlerContext, msg: Any) {
        if(msg is LoginRequest) {
            val loginService: AuthService = ServiceManager.getService(AuthService::class.java) as AuthService
            loginService.queueLoginRequest(msg)
        }
    }

    override fun terminate() {

    }
}