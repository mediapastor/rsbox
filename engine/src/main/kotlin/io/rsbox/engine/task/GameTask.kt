package io.rsbox.engine.task

import io.rsbox.engine.model.world.World
import io.rsbox.engine.service.impl.GameService

/**
 * @author Kyle Escobar
 */

interface GameTask {
    fun execute(world: World, service: GameService)
}