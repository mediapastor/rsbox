package io.rsbox.engine.model.world

import io.rsbox.engine.GameContext
import io.rsbox.engine.model.entity.LivingEntityList
import io.rsbox.engine.model.entity.Npc
import io.rsbox.engine.model.entity.Player
import io.rsbox.engine.service.ServiceProvider
import io.rsbox.engine.service.impl.XteaKeyService
import net.runelite.cache.fs.Store

/**
 * @author Kyle Escobar
 */

class World(val gameContext: GameContext) {

    /**
     * Runelite cache store for cache
     */
    lateinit var cacheStore: Store

    /**
     * Incrementing cycle
     */
    var cycleCounter = 0

    /**
     * List of current players in the world
     */
    val players = LivingEntityList(arrayOfNulls<Player>(gameContext.playerLimit))

    /**
     * List of current npcs in the world.
     */
    val npcs = LivingEntityList(arrayOfNulls<Npc>(Short.MAX_VALUE.toInt()))

    /**
     * Used frequently, declared here for performance reasons.
     */
    var xteaKeyService: XteaKeyService? = null

    lateinit var serviceProvider: ServiceProvider


    internal fun init() {

    }

    internal fun postLoad() {

    }

    internal fun cycle() {

    }

    internal fun register(player: Player): Boolean {
        val registered = players.add(player)
        if(registered) {
            player.lastIndex = player.index
            return true
        }
        return false
    }
}