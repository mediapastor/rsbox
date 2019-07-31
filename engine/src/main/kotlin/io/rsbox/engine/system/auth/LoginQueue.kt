@file:Suppress("UNUSED_VALUE", "ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")

package io.rsbox.engine.system.auth

import io.netty.channel.ChannelFutureListener
import io.rsbox.api.RSBox
import io.rsbox.api.net.login.LoginRequest
import io.rsbox.engine.model.entity.Client
import io.rsbox.engine.service.impl.LoginService
import io.rsbox.net.codec.login.LoginResponse
import io.rsbox.net.codec.login.LoginResultType
import io.rsbox.util.IsaacRandom
import mu.KotlinLogging
import kotlin.properties.Delegates

/**
 * @author Kyle Escobar
 */

class LoginQueue(private val loginService: LoginService) : Runnable {

    override fun run() {
        while(true) {
            val request = loginService.loginQueue.take()
            try {
                val decodeRandom = IsaacRandom(request.xteaKeys)
                val encodeRandom = IsaacRandom(IntArray(request.xteaKeys.size) { request.xteaKeys[it] + 50 })

                val client = Client(request.channel)
                var loadResult: PlayerLoadResult by Delegates.observable(PlayerLoadResult.INVALID_CREDENTIALS) { _, _, newValue ->
                    playerLoadObserve(newValue, request, client, encodeRandom, decodeRandom)
                }

                loadResult = PlayerSaveProvider.loadPlayer(client, request)

            } catch(e : Exception) {
                logger.error("Error occurred when handling queued login request.", e)
            }

        }
    }

    private fun playerLoadObserve(loadResult: PlayerLoadResult, request: LoginRequest, client: Client, encodeRandom: IsaacRandom, decodeRandom: IsaacRandom) {
        val loginResult: LoginResultType
        when(loadResult) {
            PlayerLoadResult.NO_ACCOUNT -> {
                loginResult = if(RSBox.server.config["server.auto_create_account"]) {
                    if(PlayerSaveProvider.createPlayer(client, request)) {
                        logger.info { "Created new player save for [username=${request.username}]" }
                        LoginResultType.ACCEPTABLE
                    } else {
                        logger.error { "Failed to create player save for [username=${request.username}]" }
                        LoginResultType.INVALID_CREDENTIALS
                    }
                } else {
                    LoginResultType.INVALID_CREDENTIALS
                }
            }

            PlayerLoadResult.ACCEPTED -> {
                loginResult = LoginResultType.ACCEPTABLE
            }

            else -> {
                loginResult = LoginResultType.INVALID_CREDENTIALS
            }
        }

        when(loginResult) {
            LoginResultType.ACCEPTABLE -> {
                client.channel.write(LoginResponse(client.index, client.privilege))
                loginService.loginGameClient(client, encodeRandom, decodeRandom)
            }

            else -> {
                request.channel.writeAndFlush(loginResult).addListener(ChannelFutureListener.CLOSE)
                logger.info { "Login request [username=${request.username}] rejected with cause $loginResult."}
            }
        }
    }

    companion object {
        val logger = KotlinLogging.logger {}
    }
}