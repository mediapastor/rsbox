package io.rsbox.api.event

import io.rsbox.api.entity.Player

/**
 * @author Kyle Escobar
 */

class PlayerLoadEvent(val player: Player) : Event() {
    override fun handler(): Boolean {
        return true
    }
}