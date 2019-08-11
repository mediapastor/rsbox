package io.rsbox.server.task

import io.rsbox.server.model.entity.Player
import io.rsbox.server.model.world.World
import io.rsbox.server.service.impl.GameService

/**
 * @author Kyle Escobar
 */

class MessageHandlerTask : GameTask {
    override fun execute(world: World, service: GameService) {
        world.players.forEach { _, player ->
            (player as Player).handleMessages()
        }
    }
}