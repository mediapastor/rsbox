package io.rsbox.server.net.protocol.impl

import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.rsbox.server.model.entity.Client
import io.rsbox.server.model.world.World
import io.rsbox.server.net.packet.Message
import io.rsbox.server.net.packet.MessageHandler
import io.rsbox.server.net.packet.builder.GamePacket
import io.rsbox.server.net.packet.builder.GamePacketReader
import io.rsbox.server.net.protocol.ServerProtocol
import io.rsbox.server.service.ServiceManager
import io.rsbox.server.service.impl.GameService
import mu.KLogging
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue

/**
 * @author Kyle Escobar
 */

class GameProtocol(channel: Channel, val client: Client) : ServerProtocol(channel) {

    private val world = client.world
    private val gameService: GameService = ServiceManager.getService(GameService::class.java)

    private val messages: BlockingQueue<MessageHandle> = ArrayBlockingQueue<MessageHandle>(90)

    override fun receiveMessage(ctx: ChannelHandlerContext, msg: Any) {
        if(msg is GamePacket) {
            val decoder = gameService.decoders.get(msg.opcode)
            if(decoder == null) {
                logger.warn { "No decoder found for message $msg." }
                return
            }

            val handler = gameService.decoders.getHandler(msg.opcode)
            if(handler == null) {
                logger.warn("No handler found for message $msg.")
                return
            }
            val message = decoder.decode(msg.opcode, gameService.messages.get(msg.opcode)!!, GamePacketReader(msg))
            messages.add(MessageHandle(message, handler, msg.opcode, msg.payload.readableBytes()))

            msg.payload.release()
        }
    }

    override fun terminate() {

    }

    /**
     * Packet Methods
     */
    fun handleMessages() {
        for(i in 0 until 120) {
            val next = messages.poll() ?: break
            next.handler.handle(client, (world as World), next.message)
        }
    }

    fun write(message: Message) {
        channel.write(message)
    }

    fun flush() {
        if(channel.isActive) {
            channel.flush()
        }
    }

    fun close() {
        channel.disconnect()
    }

    private data class MessageHandle(val message: Message, val handler: MessageHandler<Message>, val opcode: Int, val length: Int)

    companion object : KLogging()
}