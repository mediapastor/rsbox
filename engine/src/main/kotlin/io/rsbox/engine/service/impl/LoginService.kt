package io.rsbox.engine.service.impl

import com.google.common.util.concurrent.ThreadFactoryBuilder
import io.rsbox.api.Server
import io.rsbox.engine.service.Service
import io.rsbox.engine.system.auth.LoginQueue
import io.rsbox.api.net.login.LoginRequest
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
            ThreadFactoryBuilder().setNameFormat("auth-queue").setUncaughtExceptionHandler { t, e -> logger.error("Error in thread $t", e)}.build())

        for(i in 0 .. loginThreads) {
            executor.execute(LoginQueue(this))
        }

    }

    override fun onStop(server: Server) {

    }

    fun queueLoginRequest(request: LoginRequest) {
        loginQueue.offer(request)
        logger.info("Login request received and has been queued. [username=${request.username}].")
    }
}