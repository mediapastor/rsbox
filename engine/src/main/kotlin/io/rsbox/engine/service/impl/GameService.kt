package io.rsbox.engine.service.impl

import com.google.common.util.concurrent.ThreadFactoryBuilder
import io.rsbox.api.RSBox
import io.rsbox.api.Server
import io.rsbox.engine.model.RSWorld
import io.rsbox.engine.packets.PacketStructureSet
import io.rsbox.engine.service.Service
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * @author Kyle Escobar
 */

class GameService : Service() {
    lateinit var world: RSWorld

    var maxPacketsPerCycle = 30

    private val executor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor(
        ThreadFactoryBuilder()
            .setNameFormat("GAME")
            .setUncaughtExceptionHandler { t, e -> logger.error("Error in thread $t", e) }
            .build()
    )

    private val gameThreadJobs = ConcurrentLinkedQueue<() -> Unit>()

    private var debugTick = 0

    private var cycleTime = 0

    val dispatcher: CoroutineDispatcher = executor.asCoroutineDispatcher()

    internal var totalPlayerQueues = 0
    internal var totalNpcQueues = 0
    internal var totalWorldQueues = 0

    /**
     * Packet definitions
     * These sets hold all the information on how to process and
     * handle the different game packets sent to / from the game
     * clients.
     */
    internal val packetStructures = PacketStructureSet()

    override fun onStart(server: Server) {
        this.world = RSBox.server.world as RSWorld

        executor.scheduleAtFixedRate(this::cycle, 0, world.gameContext.cycleTime.toLong(), TimeUnit.MILLISECONDS)
    }

    override fun onStop(server: Server) {

    }

    private fun cycle() {

    }
}