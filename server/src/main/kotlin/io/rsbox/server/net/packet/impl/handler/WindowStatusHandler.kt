package io.rsbox.server.net.packet.impl.handler

import io.rsbox.server.model.World
import io.rsbox.server.model.entity.Client
import io.rsbox.server.net.packet.MessageHandler
import io.rsbox.server.net.packet.impl.message.WindowStatusMessage

/**
 * @author Kyle Escobar
 */

class WindowStatusHandler : MessageHandler<WindowStatusMessage> {
    override fun handle(client: Client, world: World, message: WindowStatusMessage) {
        client.clientWidth = message.width
        client.clientHeight = message.height
    }
}