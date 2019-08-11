package io.rsbox.server.service.impl

import com.google.common.util.concurrent.ThreadFactoryBuilder
import io.netty.channel.ChannelFutureListener
import io.rsbox.server.Server
import io.rsbox.server.config.SettingsSpec
import io.rsbox.server.model.entity.Client
import io.rsbox.server.net.GameHandler
import io.rsbox.server.net.GameMessageEncoder
import io.rsbox.server.net.PacketMetadata
import io.rsbox.api.ServerResultType
import io.rsbox.server.net.game.GamePacketDecoder
import io.rsbox.server.net.game.GamePacketEncoder
import io.rsbox.server.net.login.LoginRequest
import io.rsbox.server.net.login.LoginResponse
import io.rsbox.server.net.protocol.impl.GameProtocol
import io.rsbox.server.serializer.player.PlayerLoadResult
import io.rsbox.server.serializer.player.PlayerSave
import io.rsbox.server.service.Service
import io.rsbox.server.service.ServiceManager
import io.rsbox.server.util.IsaacRandom
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

    private fun successfulLogin(client: Client, request: LoginRequest) {
        val gameProtocol = GameProtocol(request.channel)
        client.gameProtocol = gameProtocol
        client.channel.attr(GameHandler.PROTOCOL_KEY).set(gameProtocol)

        val gameService = ServiceManager.getService(GameService::class.java)

        val p = client.channel.pipeline()

        val decodeRandom = IsaacRandom(request.xteaKeys)
        val encodeRandom = IsaacRandom(IntArray(request.xteaKeys.size) { request.xteaKeys[it] + 50 })

        if(client.channel.isActive) {
            p.remove("handshake_encoder")
            p.remove("login_decoder")
            p.remove("login_encoder")

            p.addFirst("packet_encoder", GamePacketEncoder(encodeRandom))
            p.addAfter("packet_encoder", "message_encoder", GameMessageEncoder(gameService.encoders, gameService.messages))
            p.addBefore("handler", "packet_decoder", GamePacketDecoder(decodeRandom, PacketMetadata(gameService.messages)))
            client.login()
            client.channel.flush()
        }
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
                        if(Server.settings[SettingsSpec.auto_create_users]) {
                            logger.info { "Login request from user ${request.username} does not have an account." }
                            PlayerSave.create(request)
                            logger.info { "Created account for user ${request.username}. Continuing to login."}
                            serverResult = ServerResultType.ACCEPTABLE
                        } else {
                            serverResult = ServerResultType.INVALID_CREDENTIALS
                        }
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
                    ServerResultType.ACCEPTABLE -> {
                        val client = Client(request.channel)
                        client.init(request)
                        client.channel.write(LoginResponse(index = client.index, privilege = client.privilege))
                        successfulLogin(client, request)
                        logger.info { "Login request accepted for user ${request.username}." }
                    }
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