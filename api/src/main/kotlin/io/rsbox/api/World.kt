package io.rsbox.api

import io.rsbox.api.entity.LivingEntity
import io.rsbox.api.entity.Player

/**
 * @author Kyle Escobar
 */

interface World {
    val players: HashMap<Int, LivingEntity>

    val npcs: HashMap<Int, LivingEntity>

    fun register(player: Player): Boolean
}