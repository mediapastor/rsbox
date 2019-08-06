package io.rsbox.server.net

import mu.KLogging
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.ReadableByteChannel
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.util.concurrent.ExecutorService

/**
 * @author Kyle Escobar
 */

class NetworkHandler(val service: ExecutorService) {
    fun connect(key: SelectionKey) {
    }

    fun accept(key: SelectionKey, selector: Selector) {
        val socket = (key.channel() as ServerSocketChannel).accept()
        socket.configureBlocking(false)
        socket.socket().tcpNoDelay = true
        socket.register(selector, SelectionKey.OP_READ)
    }

    fun read(key: SelectionKey) {
        val channel = key.channel() as ReadableByteChannel
        val buffer = ByteBuffer.allocate(100_100)
        val session = key.attachment() as NetworkSession

        if(channel.read(buffer) == -1) {
            throw IOException("An existing connection was dropped.")
        }

        buffer.flip()

        if(session == null) {
            key.attach(NetworkSession(key,service))
        }

        service.execute(session.protocol.invokeReader(session, buffer))
    }

    fun write(key: SelectionKey) {

    }

    fun disconnect(key: SelectionKey) {

    }

    companion object : KLogging()
}