package io.rsbox.game

import io.rsbox.api.EventListener
import io.rsbox.api.entity.Player
import io.rsbox.api.event.PlayerLoadEvent

/**
 * @author Kyle Escobar
 */

object Game {
    fun init() {
        EventListener.on_event(PlayerLoadEvent::class) {
            setupGameWindows(player)
        }
    }

    private fun setupGameWindows(player: Player) {
    }
}