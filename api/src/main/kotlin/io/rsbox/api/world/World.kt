package io.rsbox.api.world

import io.rsbox.api.entity.LivingEntity
import io.rsbox.api.entity.Player

/**
 * @author Kyle Escobar
 */

interface World {
    fun init()

    fun load()

    fun unload()

    fun register(player: Player): Boolean
}