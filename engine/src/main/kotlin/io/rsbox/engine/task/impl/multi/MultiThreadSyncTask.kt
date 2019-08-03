package io.rsbox.engine.task.impl.multi

import io.rsbox.engine.model.entity.LivingEntity
import io.rsbox.engine.model.world.World
import io.rsbox.engine.service.impl.GameService
import io.rsbox.engine.sync.SyncTask
import io.rsbox.engine.sync.task.PlayerPreSyncTask
import io.rsbox.engine.task.GameTask
import java.util.concurrent.ExecutorService
import java.util.concurrent.Phaser

/**
 * @author Kyle Escobar
 */

class MultiThreadSyncTask(private val executor: ExecutorService) : GameTask {
    private val phaser = Phaser(1)

    override fun execute(world: World, service: GameService) {
        val players = world.players
        val playerCount = players.count()
        val npcs = world.npcs
        val rawNpcs = world.npcs.entries
        val npcCount = npcs.count()

        phaser.bulkRegister(playerCount)

        players.forEach { p ->
            distribute(phaser, executor, p, PlayerPreSyncTask)
        }
        phaser.arriveAndAwaitAdvance()
    }

    private fun <T : LivingEntity> distribute(phaser: Phaser, executor: ExecutorService, entity: T, task: SyncTask<T>) {
        executor.execute {
            try {
                task.run(entity)
            } catch(e : Exception) {
            } finally {
                phaser.arriveAndDeregister()
            }
        }
    }
}