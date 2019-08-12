package io.rsbox.api.event

import io.rsbox.api.ServerResultType
import io.rsbox.api.entity.Player

/**
 * @author Kyle Escobar
 */

class PlayerLoginEvent(val player: Player) : Event(), Cancelable {
    override var cancelled: Boolean = false

    var returnServerResult: ServerResultType = ServerResultType.COULD_NOT_COMPLETE_LOGIN

    override fun handler(): Boolean {
        return cancelled
    }
}