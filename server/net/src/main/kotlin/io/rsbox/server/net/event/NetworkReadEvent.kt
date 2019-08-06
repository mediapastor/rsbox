package io.rsbox.server.net.event

import io.rsbox.server.net.NetworkSession
import java.nio.ByteBuffer

/**
 * @author Kyle Escobar
 */

abstract class NetworkReadEvent(val session: NetworkSession, var buffer: ByteBuffer) : Runnable {

    var usedQueuedBuffer: Boolean = false

    override fun run() {
        try {
            if(session.readQueue != null) {
                buffer = session.readQueue!!.put(buffer)
                buffer.flip()
                session.readQueue = null
                usedQueuedBuffer = true
            }

            read(session, buffer)
        } catch(e: Throwable) {
            session.disconnect()
        }
    }

    fun queueBuffer(vararg data: Int) {
        val queue = ByteBuffer.allocate(data.size + buffer.remaining() + 100_000)
        data.forEach { value ->
            queue.put(value.toByte())
        }
        queue.put(buffer)
        session.readQueue = queue
    }

    abstract fun read(session: NetworkSession, buffer: ByteBuffer)
}