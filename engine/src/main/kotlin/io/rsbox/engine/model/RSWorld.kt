package io.rsbox.engine.model

import io.rsbox.api.World
import io.rsbox.engine.GameContext
import io.rsbox.engine.model.entity.LivingEntityList
import io.rsbox.engine.model.entity.Npc
import io.rsbox.engine.model.entity.Player
import net.runelite.cache.fs.Store

/**
 * @author Kyle Escobar
 */

class RSWorld(val gameContext: GameContext) : World {

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