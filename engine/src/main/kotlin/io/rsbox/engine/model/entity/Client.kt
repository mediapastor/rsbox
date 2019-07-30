package io.rsbox.engine.model.entity

import io.netty.channel.Channel
import io.rsbox.api.net.login.LoginRequest

/**
 * @author Kyle Escobar
 */

open class Client(val channel: Channel) : Player() {
    var focused: Boolean = true

    var width: Int = 765

    var height: Int = 503

    var cameraPitch: Int = 0

    var cameraYaw: Int = 0

    companion object {
        fun fromLoginRequest(request: LoginRequest) : Client {
            val client = Client(request.channel)
            client.width = request.clientWidth
            client.height = request.clientHeight
            client.username = request.username
            client.uuid = request.uuid
            client.currentXteaKeys = request.xteaKeys
            return client
        }
    }
}