package io.rsbox.engine.system.game

import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.rsbox.engine.Server
import io.rsbox.engine.service.impl.GameService
import io.rsbox.engine.net.packet.PacketHandle
import io.rsbox.engine.net.packet.GamePacket
import io.rsbox.engine.net.packet.Packet
import io.rsbox.engine.model.world.World
import io.rsbox.engine.model.entity.Client
import io.rsbox.engine.net.packet.GamePacketReader
import io.rsbox.engine.system.ServerSystem
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue

/**
 * @author Kyle Escobar
 */

class GameSystem(channel: Channel, val world: World, val client: Client, val service: GameService) : ServerSystem(channel) {

    private val packetQueue: BlockingQueue<PacketHandle> = ArrayBlockingQueue<PacketHandle>(30)

    override fun recieveMessage(ctx: ChannelHandlerContext, msg: Any) {
        if(msg is GamePacket) {
            val decoder = service.packetDecoders.get(msg.opcode)
            if(decoder == null) {
                Server.logger.warn { "No decoder found for packet $msg." }
                return
            }

            val handler = service.packetDecoders.getHandler(msg.opcode)
            if(handler == null) {
                Server.logger.warn { "No handler found for packet $msg." }
                return
            }

            val packet = decoder.decode(msg.opcode, service.packetStructures.get(msg.opcode)!!, GamePacketReader(msg))
            packetQueue.add(PacketHandle(packet, handler, msg.opcode, msg.payload.readableBytes()))

            /**
             * Release the buffer for the [GamePacket]
             */
            msg.payload.release()
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
        println("Sent packet ${packet.javaClass.simpleName} to ${client.username}'s gameclient.")
    }

    fun flush() {
        if(channel.isActive) channel.flush()
    }

    fun close() {
        channel.disconnect()
    }
}