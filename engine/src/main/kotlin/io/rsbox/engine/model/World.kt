package io.rsbox.engine.model

import io.rsbox.api.World
import io.rsbox.engine.GameContext
import net.runelite.cache.fs.Store

/**
 * @author Kyle Escobar
 */

class World(val gameContext: GameContext) : World {

    lateinit var cacheStore: Store

    var cycleCounter = 0

    internal fun init() {

    }

    internal fun postLoad() {

    }

    internal fun cycle() {

    }
}