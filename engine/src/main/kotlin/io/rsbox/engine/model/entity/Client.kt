package io.rsbox.engine.model.entity

import io.netty.channel.Channel
import io.rsbox.engine.net.packet.Packet
import io.rsbox.engine.serialization.nbt.NBTTag
import io.rsbox.engine.system.game.GameSystem

/**
 * @author Kyle Escobar
 */

class Client(val channel: Channel) : Player() {

    @NBTTag("username")
    lateinit var username: String

    @NBTTag("password")
    lateinit var password: String

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