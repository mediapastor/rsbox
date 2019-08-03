package io.rsbox.engine.task.impl.single

import io.rsbox.engine.model.world.World
import io.rsbox.engine.service.impl.GameService
import io.rsbox.engine.sync.task.PlayerPreSyncTask
import io.rsbox.engine.task.GameTask

/**
 * @author Kyle Escobar
 */

class SingleThreadSyncTask : GameTask {
    override fun execute(world: World, service: GameService) {
        val players = world.players
        val npcs = world.npcs
        val rawNpcs = world.npcs.entries

        players.forEach { p ->
            PlayerPreSyncTask.run(p)
        }
    }
}