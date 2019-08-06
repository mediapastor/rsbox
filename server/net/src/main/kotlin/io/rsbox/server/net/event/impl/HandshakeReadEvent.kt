package io.rsbox.server.net.event.impl

import io.rsbox.server.net.NetworkReactor
import io.rsbox.server.net.NetworkSession
import io.rsbox.server.net.event.NetworkReadEvent
import mu.KLogging
import java.nio.ByteBuffer
import kotlin.experimental.and
import kotlin.random.Random

/**
 * @author Kyle Escobar
 */

class HandshakeReadEvent(session: NetworkSession, buffer: ByteBuffer) : NetworkReadEvent(session, buffer) {
    override fun read(session: NetworkSession, buffer: ByteBuffer) {
        val opcode: Int = (buffer.get() and 0xFF.toByte()).toInt()
        when(opcode) {
            14 -> {
                session.serverKey = Random.nextLong()
                //session.write(true)
                return
            }

            15 -> {
                val revision = buffer.int
                if(revision != NetworkReactor.revision) {
                    logger.info { "Client ${session.address} connection rejected -> revision mismatch." }
                    session.disconnect()
                    return
                }
                //session.write(false)
            }

            else -> {
                session.disconnect()
                return
            }
        }
    }

    companion object : KLogging()
}