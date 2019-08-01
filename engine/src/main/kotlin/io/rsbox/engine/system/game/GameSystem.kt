package io.rsbox.engine.system.game

import io.netty.channel.Channel
import io.rsbox.api.World
import io.rsbox.engine.service.impl.GameService

/**
 * @author Kyle Escobar
 */

class GameSystem(channel: Channel, val world: World, val gameService: GameService) {
}