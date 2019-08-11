package io.rsbox.server.model

import io.rsbox.server.Server
import io.rsbox.server.model.entity.LivingEntity
import io.rsbox.server.model.entity.Player
import java.security.SecureRandom
import java.util.*

/**
 * @author Kyle Escobar
 */

class World(val server: Server) : io.rsbox.api.World {

    val players: HashMap<Int, LivingEntity> = hashMapOf()

    val npcs: HashMap<Int, LivingEntity> = hashMapOf()

    val random: Random = SecureRandom()

    var currentCycle = 0


    /**
     * World base methods
     */
    fun init() {

    }

    fun load() {

    }

    fun unload() {

    }


    /**
     * Registers player objects with the world.
     * Sets the player index.
     */
    fun register(player: Player): Boolean {
        players[getAvailablePlayerIndex()] = player
        return true
    }

    private fun getAvailablePlayerIndex(): Int {
        var maxIndex = 0
        players.forEach { index, _ ->
            if(maxIndex == 0 || maxIndex > index) {
                maxIndex = index
            }
        }

        return maxIndex + 1
    }
}