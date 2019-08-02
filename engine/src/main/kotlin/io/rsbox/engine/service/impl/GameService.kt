package io.rsbox.engine.service.impl

import com.google.common.util.concurrent.ThreadFactoryBuilder
import io.rsbox.api.RSBox
import io.rsbox.api.Server
import io.rsbox.engine.model.world.RSWorld
import io.rsbox.engine.packets.PacketDecoderSet
import io.rsbox.engine.packets.PacketEncoderSet
import io.rsbox.engine.packets.PacketStructureSet
import io.rsbox.engine.service.Service
import io.rsbox.engine.task.GameTask
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
    /**
     * Pause game
     */
    var paused: Boolean = false

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
     * Game tasks
     */
    private val tasks = mutableListOf<GameTask>()

    /**
     * Packet definitions
     * These sets hold all the information on how to process and
     * handle the different game packets sent to / from the game
     * clients.
     */
    internal val packetStructures = PacketStructureSet()
    internal val packetEncoders = PacketEncoderSet()
    internal val packetDecoders = PacketDecoderSet()

    override fun onStart(server: Server) {
        this.world = RSBox.server.world as RSWorld
        this.loadTasks()
        executor.scheduleAtFixedRate(this::cycle, 0, world.gameContext.cycleTime.toLong(), TimeUnit.MILLISECONDS)
    }

    override fun onStop(server: Server) {

    }

    private fun loadTasks() {
        val processors = Runtime.getRuntime().availableProcessors()
        val singleThreadTasks = processors == 1

        if(singleThreadTasks) {
            /**
             * Add Sequential Tasks
             */
            tasks.addAll(arrayOf(
            ))
        } else {
            val executor = Executors.newFixedThreadPool(processors,
                    ThreadFactoryBuilder()
                        .setNameFormat("game-thread")
                        .setUncaughtExceptionHandler { t, e -> logger.error("Error in thread $t", e) }
                        .build()
                )

            tasks.addAll(arrayOf(

            ))
        }
    }

    fun createGameThreadJob(job: Function0<Unit>) {
        gameThreadJobs.offer(job)
    }

    private fun cycle() {
        if(paused) return

        gameThreadJobs.forEach { job ->
            try {
                job()
            } catch (e : Exception) {
                logger.error("Error executing game-thread job.", e)
            }
        }

        gameThreadJobs.clear()

        tasks.forEach { task ->
            try {
                task.execute(world, this)
            } catch(e : Exception) {
                logger.error("Error with task ${task.javaClass.simpleName}.", e)
            }
        }


    }
}