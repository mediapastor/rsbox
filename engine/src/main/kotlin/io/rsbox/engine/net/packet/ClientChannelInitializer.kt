package io.rsbox.engine.net.packet

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.traffic.ChannelTrafficShapingHandler
import io.netty.handler.traffic.GlobalTrafficShapingHandler
import io.rsbox.engine.system.game.GameHandler
import io.rsbox.engine.net.protocol.HandshakeDecoder
import io.rsbox.engine.net.protocol.HandshakeEncoder
import net.runelite.cache.fs.Store
import java.math.BigInteger
import java.util.concurrent.Executors

/**
 * @author Kyle Escobar
 */

class ClientChannelInitializer(
    private val revision: Int,
    private val rsaExponent: BigInteger,
    private val rsaModulus: BigInteger,
    private val filestore: Store
    ) : ChannelInitializer<SocketChannel>() {

    private val globalTrafficShaper = GlobalTrafficShapingHandler(Executors.newSingleThreadScheduledExecutor(), 0, 0, 1000)

    private val handler = GameHandler(filestore)

    override fun initChannel(ch: SocketChannel) {
        val p = ch.pipeline()
        val crcs = filestore.indexes.map { it.crc }.toIntArray()

        p.addLast("global_traffic", globalTrafficShaper)
        p.addLast("channel_traffic", ChannelTrafficShapingHandler(0, 1024 * 5, 1000))
        p.addLast("handshake_encoder", HandshakeEncoder())
        p.addLast("handshake_decoder", HandshakeDecoder(revision, crcs, rsaExponent, rsaModulus))
        p.addLast("handler", handler)
    }
}