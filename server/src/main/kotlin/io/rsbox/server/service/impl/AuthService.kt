package io.rsbox.server.service.impl

import com.google.common.util.concurrent.ThreadFactoryBuilder
import io.netty.channel.ChannelFutureListener
import io.rsbox.server.net.ServerResultType
import io.rsbox.server.net.login.LoginRequest
import io.rsbox.server.serializer.player.PlayerLoadResult
import io.rsbox.server.serializer.player.PlayerSave
import io.rsbox.server.service.Service
import mu.KLogging
import java.lang.Exception
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue

/**
 * @author Kyle Escobar
 */

class AuthService : Service() {

    private val loginQueue = LinkedBlockingQueue<LoginRequest>()

    /**
     * Thread stuff
     */
    private val loginThreads = 2
    private val loginExecutor = Executors.newFixedThreadPool(loginThreads,
        ThreadFactoryBuilder()
            .setNameFormat("LOGIN-QUEUE")
            .setUncaughtExceptionHandler { t, e -> logger.error("Uncaught exception in thread $t", e) }.build()
    )

    override fun onStart() {
        /**
         * Start login process threads
         */
        for(i in 0 until loginThreads) {
            loginExecutor.execute { this.loginCycle() }
        }
    }

    override fun onStop() {

    }

    fun queueLoginRequest(request: LoginRequest) {
        loginQueue.offer(request)
    }


    /**
     * Login cycle.
     * Waits for login requests to be queued and then processes them.
     */
    private fun loginCycle() {
        while(true) {
            val request = loginQueue.take()

            try {
                lateinit var serverResult: ServerResultType
                val playerLoadResult = PlayerSave.check(request.username, request.password)
                when(playerLoadResult) {
                    PlayerLoadResult.NEW_ACCOUNT -> {
                        serverResult = ServerResultType.COULD_NOT_COMPLETE_LOGIN
                    }

                    PlayerLoadResult.INVALID -> {
                        serverResult = ServerResultType.INVALID_CREDENTIALS
                    }

                    PlayerLoadResult.ACCEPTABLE -> {
                        serverResult = ServerResultType.ACCEPTABLE
                    }

                    else -> {
                        serverResult = ServerResultType.COULD_NOT_COMPLETE_LOGIN
                    }
                }


                when(serverResult) {
                    ServerResultType.ACCEPTABLE -> {}
                    else -> {
                        request.channel.writeAndFlush(serverResult).addListener { ChannelFutureListener.CLOSE }
                        logger.info { "Login request denied for user ${request.username} with result ${serverResult}." }
                    }
                }

            } catch (e : Exception) {
                logger.error("An error occurred when processing login request in channel ${request.channel}.", e)
            }
        }
    }

    companion object : KLogging()
}