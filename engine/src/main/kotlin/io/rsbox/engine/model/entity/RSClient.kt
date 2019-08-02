package io.rsbox.engine.model.entity

import io.netty.channel.Channel
import io.rsbox.api.entity.Client
import io.rsbox.api.net.packet.Packet
import io.rsbox.engine.system.game.GameSystem

/**
 * @author Kyle Escobar
 */

class RSClient(val channel: Channel) : Player(), Client {

    lateinit var gameSystem: GameSystem

    var clientWidth = 765

    var clientHeight = 503

    var cameraPitch = 0

    var cameraYaw = 0

    override fun handleIngressPackets() {
        gameSystem.handleIngressPackets()
    }

    override fun sendPacket(vararg packets: Packet) {
        packets.forEach { p -> gameSystem.write(p) }
    }
}