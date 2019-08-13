package io.rsbox.game

import io.rsbox.api.EventListener
import io.rsbox.api.consts.Varbit
import io.rsbox.api.consts.Varp
import io.rsbox.api.entity.Player
import io.rsbox.api.event.PlayerLoadEvent
import io.rsbox.api.inter.InterfaceDestination

/**
 * @author Kyle Escobar
 */

object Game {
    fun init() {
        EventListener.on_event(PlayerLoadEvent::class) {
            setupGameFrame(player)
        }
    }

    private fun setupGameFrame(player: Player) {
        player.openOverlayInterface(player.interfaces.displayMode)
        InterfaceDestination.values.filter { pane -> pane.interfaceId != -1 }.forEach { pane ->
            if(pane == InterfaceDestination.XP_COUNTER && player.getVarbit(Varbit.XP_DROPS_VISIBLE) == 0) {
                return@forEach
            } else if(pane == InterfaceDestination.MINI_MAP && player.getVarbit(Varbit.HIDE_DATA_ORBS) == 1) {
                return@forEach
            }
            player.openInterface(pane.interfaceId, pane)
        }

        val displayName = player.displayName.isNotBlank()
        player.runClientScript(1105, if(displayName) 1 else 0)
        player.runClientScript(423, player.displayName)
        if(player.getVarp(1055) == 0 && displayName) {
            player.syncVarp(1055)
        }
        player.setVarbit(8119, 1)

        player.syncVarp(Varp.NPC_ATTACK_PRIORITY)
        player.syncVarp(Varp.PLAYER_ATTACK_PRIORITY)
    }
}