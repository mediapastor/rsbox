package io.rsbox.engine.task.impl

import io.rsbox.engine.model.world.World
import io.rsbox.engine.service.impl.GameService
import io.rsbox.engine.task.GameTask

/**
 * @author Kyle Escobar
 */

class PacketHandlerTask : GameTask {
    override fun execute(world: World, service: GameService) {
        world.players.forEach { p ->
            p.handleIngressPackets()
        }
    }
}