package io.rsbox.server.model.entity

import io.rsbox.server.Launcher
import io.rsbox.server.Server
import io.rsbox.server.model.World

/**
 * @author Kyle Escobar
 */

open class Player : LivingEntity(), io.rsbox.api.entity.Player {
    var username = ""

    var displayName = ""

    var password = ""

    var uuid = ""

    var privilege = 0

    lateinit var client: Client

    var lastIndex = -1

    val world: World = Launcher.server.world

    val server: Server = Launcher.server
}