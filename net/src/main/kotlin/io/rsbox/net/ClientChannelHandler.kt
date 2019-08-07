package io.rsbox.net

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.timeout.IdleStateHandler
import io.netty.handler.traffic.ChannelTrafficShapingHandler
import io.netty.handler.traffic.GlobalTrafficShapingHandler
import io.rsbox.net.handshake.HandshakeDecoder
import io.rsbox.net.handshake.HandshakeEncoder
import java.util.concurrent.Executors

/**
 * @author Kyle Escobar
 */

class ClientChannelHandler : ChannelInitializer<SocketChannel>() {
    private val traficShaper = GlobalTrafficShapingHandler(Executors.newSingleThreadScheduledExecutor(), 0, 0, 1000)

    override fun initChannel(ch: SocketChannel) {
        val p = ch.pipeline()

        p.addLast("global_traffic", traficShaper)
        p.addLast("channel_traffic", ChannelTrafficShapingHandler(0,1024 * 5, 1000))
        p.addLast("timeout", IdleStateHandler(30, 0, 0))
        p.addLast("handshake_encoder", HandshakeEncoder())
        p.addLast("handshake_decoder", HandshakeDecoder())
        p.addLast("handler", GameHandler())
    }
}