package io.rsbox.server.model.world

import io.rsbox.server.Server
import io.rsbox.server.model.entity.Player
import io.rsbox.server.service.impl.XteaKeyService
import java.security.SecureRandom
import java.util.*

/**
 * @author Kyle Escobar
 */

class World(val server: Server) : io.rsbox.api.World {

    override val players: HashMap<Int, io.rsbox.api.entity.LivingEntity> = hashMapOf()

    override val npcs: HashMap<Int, io.rsbox.api.entity.LivingEntity> = hashMapOf()

    val random: Random = SecureRandom()

    var currentCycle = 0

    lateinit var xteaKeyService: XteaKeyService


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

    override fun register(player: io.rsbox.api.entity.Player): Boolean {
        return this.register(player as Player)
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