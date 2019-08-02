package io.rsbox.engine.system.game

import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.rsbox.engine.service.impl.GameService
import io.rsbox.engine.net.packet.PacketHandle
import io.rsbox.engine.net.packet.GamePacket
import io.rsbox.engine.net.packet.Packet
import io.rsbox.engine.model.world.RSWorld
import io.rsbox.engine.model.entity.Client
import io.rsbox.engine.system.ServerSystem
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue

/**
 * @author Kyle Escobar
 */

class GameSystem(channel: Channel, val world: RSWorld, val client: Client, val service: GameService) : ServerSystem(channel) {

    private val packetQueue: BlockingQueue<PacketHandle> = ArrayBlockingQueue<PacketHandle>(30)

    override fun recieveMessage(ctx: ChannelHandlerContext, msg: Any) {
        if(msg is GamePacket) {
        }
    }

    override fun terminate() {}

    fun handleIngressPackets() {
        for(i in 0 until service.maxPacketsPerCycle) {
            val next = packetQueue.poll() ?: break
            next.handler.handle(client, world, next.message)
        }
    }

    fun write(packet: Packet) {
        channel.write(packet)
    }

    fun flush() {
        if(channel.isActive) channel.flush()
    }

    fun close() {
        channel.disconnect()
    }
}