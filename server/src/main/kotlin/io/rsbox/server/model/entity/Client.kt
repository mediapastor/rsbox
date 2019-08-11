package io.rsbox.server.model.entity

import com.uchuhimo.konf.Config
import io.netty.channel.Channel
import io.rsbox.server.ServerConstants
import io.rsbox.server.net.login.LoginRequest
import io.rsbox.server.net.protocol.impl.GameProtocol
import io.rsbox.server.serializer.player.PlayerSave
import io.rsbox.server.serializer.player.PlayerSpec
import java.io.File

/**
 * @author Kyle Escobar
 */

class Client(val channel: Channel) : Player() {
    init {
        this.client = this
    }

    var clientResizable = false

    var clientWidth = 0

    var clientHeight = 0

    lateinit var gameProtocol: GameProtocol

    fun register() {
        this.world.register(this)
    }

    fun init(request: LoginRequest): Client {
        PlayerSave.load(this, request)
        this.register()
        return this
    }

    fun login() {

    }
}