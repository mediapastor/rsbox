package io.rsbox.server.net

import io.rsbox.server.net.protocol.NetworkProtocol
import io.rsbox.server.net.protocol.impl.HandshakeProtocol
import mu.KLogging
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.SocketChannel
import java.util.concurrent.ExecutorService
import java.util.concurrent.locks.ReentrantLock

/**
 * @author Kyle Escobar
 */

class NetworkSession(val key: SelectionKey, val service: ExecutorService) {

    val address = getRemoteAddress().replace("/","").split(":")[0]
    var active = true
    var lastPing: Long = 0
    var serverKey: Long = 0

    val writeQueue = arrayListOf<ByteBuffer>()
    val writeLock = ReentrantLock()

    var readQueue: ByteBuffer? = null

    var protocol = HANDSHAKE

    fun disconnect() {
        try {
            if(!active) return
            active = false
            key.cancel()
            val channel = key.channel() as SocketChannel
            channel.socket().close()

            // TODO Implement player saving here

        } catch(e: IOException) {
            logger.error("Failed to properly disconnect channel.", e)
        }
    }

    private fun getRemoteAddress(): String {
        return (key.channel() as SocketChannel).remoteAddress.toString()
    }

    companion object : KLogging() {
        val HANDSHAKE: NetworkProtocol = HandshakeProtocol()
    }
}