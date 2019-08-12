package io.rsbox.server.task

import io.rsbox.server.model.world.World
import io.rsbox.server.service.impl.GameService

/**
 * @author Kyle Escobar
 */

interface GameTask {
    fun execute(world: World, service: GameService)
}