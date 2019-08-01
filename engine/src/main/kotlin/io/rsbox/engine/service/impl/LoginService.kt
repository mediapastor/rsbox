package io.rsbox.engine.service.impl

import com.google.common.util.concurrent.ThreadFactoryBuilder
import io.rsbox.api.Server
import io.rsbox.engine.service.Service
import io.rsbox.engine.system.auth.LoginQueue
import io.rsbox.api.net.login.LoginRequest
import io.rsbox.engine.model.entity.RSClient
import io.rsbox.net.codec.game.GamePacketEncoder
import io.rsbox.util.IsaacRandom
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue

/**
 * @author Kyle Escobar
 */

class LoginService : Service() {

    val loginQueue = LinkedBlockingQueue<LoginRequest>()

    private val loginThreads = 3

    override fun onStart(server: Server) {

        val executor = Executors.newFixedThreadPool(loginThreads,
            ThreadFactoryBuilder().setNameFormat("login-queue").setUncaughtExceptionHandler { t, e -> logger.error("Error in thread $t", e)}.build())

        for(i in 0 until loginThreads) {
            executor.execute(LoginQueue(this))
        }

    }

    override fun onStop(server: Server) {

    }

    fun queueLoginRequest(request: LoginRequest) {
        loginQueue.offer(request)
        logger.info("Login request received and has been queued. [username=${request.username}].")
    }

    internal fun loginGameClient(client: RSClient, encodeRandom: IsaacRandom, decodeRandom: IsaacRandom) {
        val p = client.channel.pipeline()

        if(client.channel.isActive) {

            p.remove("handshake_encoder")
            p.remove("login_decoder")
            p.remove("login_encoder")

            p.addFirst("packet_encoder", GamePacketEncoder(encodeRandom))
            p.addAfter("packet_encoder", "message_encoder", null)

            p.addBefore("handler", "packet_decoder", null)

            client.login()
            client.channel.flush()

        }
    }
}