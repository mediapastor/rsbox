package io.rsbox.engine.system.auth

import io.rsbox.engine.model.entity.Client
import io.rsbox.engine.service.impl.LoginService
import io.rsbox.net.codec.login.LoginResultType
import io.rsbox.util.IsaacRandom
import mu.KotlinLogging

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

                val client = Client.fromLoginRequest(request)
                lateinit var loginResult: LoginResultType

            } catch(e : Exception) {
                logger.error("Error occurred when handling queued login request.", e)
            }

        }
    }

    companion object {
        val logger = KotlinLogging.logger {}
    }
}