package io.rsbox.engine.system.game

import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.rsbox.api.World
import io.rsbox.engine.service.impl.GameService
import io.rsbox.api.net.packet.PacketHandle
import io.rsbox.api.net.packet.GamePacket
import io.rsbox.net.system.ServerSystem
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue

/**
 * @author Kyle Escobar
 */

class GameSystem(channel: Channel, val world: World, val service: GameService) : ServerSystem(channel) {

    private val messages: BlockingQueue<PacketHandle> = ArrayBlockingQueue<PacketHandle>(30)

    override fun recieveMessage(ctx: ChannelHandlerContext, msg: Any) {
        if(msg is GamePacket) {
        }
    }

    override fun terminate() {}
}