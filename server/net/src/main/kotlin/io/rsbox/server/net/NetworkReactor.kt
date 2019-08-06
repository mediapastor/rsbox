package io.rsbox.server.net

import mu.KLogging
import java.io.IOException
import java.net.InetSocketAddress
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.util.concurrent.Executors

/**
 * @author Kyle Escobar
 */

class NetworkReactor(val eventHandler: NetworkHandler) : Runnable {

    private var running: Boolean = false
    private val service = Executors.newSingleThreadExecutor()

    lateinit var channel: ServerSocketConnection

    fun start() {
        running = true
        service.execute(this)
    }

    override fun run() {
        while(running) {
            try {
                channel.selector.select()
            } catch(e: IOException) {
                e.printStackTrace()
            }

            val iterator = channel.selector.selectedKeys().iterator()
            while(iterator.hasNext()) {
                val key = iterator.next()
                iterator.remove()

                try {

                    if(!key.isValid || !key.channel().isOpen) {
                        key.cancel()
                        continue
                    }

                    if(key.isConnectable) {
                        logger.info { "Accepted" }
                        eventHandler.connect(key)
                    }

                    if(key.isAcceptable) {
                        eventHandler.accept(key, channel.selector)
                    }

                    if(key.isReadable) {
                        eventHandler.read(key)
                    } else if(key.isWritable) {
                        eventHandler.write(key)
                    }
                } catch(e: Throwable) {
                    eventHandler.disconnect(key)
                }
            }
        }
    }

    fun terminate() {
        running = false
    }

    companion object : KLogging() {

        var revision: Int = -1

        fun configure(port: Int): NetworkReactor {
            return configure(port, 1)
        }

        fun configure(port: Int, threads: Int): NetworkReactor {
            val reactor = NetworkReactor(NetworkHandler(Executors.newFixedThreadPool(threads)))
            val channel = ServerSocketChannel.open() ?: throw IllegalStateException("")
            val selector = Selector.open() ?: throw IllegalStateException("")
            channel.bind(InetSocketAddress(port))
            channel.configureBlocking(false)
            channel.register(selector, SelectionKey.OP_ACCEPT)
            reactor.channel = ServerSocketConnection(selector, channel)
            return reactor
        }
    }
}