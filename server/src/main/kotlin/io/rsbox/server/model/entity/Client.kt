package io.rsbox.server.model.entity

import com.uchuhimo.konf.Config
import io.netty.channel.Channel
import io.rsbox.server.ServerConstants
import io.rsbox.server.net.login.LoginRequest
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

    fun init() {
        // TODO Setup world indexes and assign a player index.
    }

    fun login(request: LoginRequest) {
        val file = File("${ServerConstants.SAVES_PATH}${PlayerSave.filterUsername(request.username)}.yml")
        val save = Config { addSpec(PlayerSpec) }.from.yaml.file(file)

        this.username = PlayerSave.filterUsername(save[PlayerSpec.username])
        this.password = save[PlayerSpec.password]
        this.displayName = save[PlayerSpec.displayName]
        this.privilege = save[PlayerSpec.privilege]
        this.uuid = save[PlayerSpec.uuid]
        this.clientResizable = request.resizableClient
        this.clientWidth = request.clientWidth
        this.clientHeight = request.clientHeight
    }
}