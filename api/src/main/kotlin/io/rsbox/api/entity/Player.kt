package io.rsbox.api.entity

import io.rsbox.api.Server
import io.rsbox.api.World

/**
 * @author Kyle Escobar
 */

interface Player : LivingEntity {
    val username: String

    val displayName: String

    val password: String

    val uuid: String

    val privilege: Int

    var server: Server

    var world: World
}