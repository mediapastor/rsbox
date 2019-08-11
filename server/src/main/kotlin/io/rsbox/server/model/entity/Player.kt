package io.rsbox.server.model.entity

import io.rsbox.server.Launcher
import io.rsbox.server.Server
import io.rsbox.server.model.world.World
import io.rsbox.server.net.packet.Message

/**
 * @author Kyle Escobar
 */

open class Player : LivingEntity(), io.rsbox.api.entity.Player {
    var username = ""

    var displayName = ""

    var password = ""

    var uuid = ""

    var privilege = 0

    lateinit var client: Client

    var lastIndex = -1

    val world: World = Launcher.server.world as World

    val server: Server = Launcher.server

    /**
     * Rendering data
     */
    internal val gpiLocalPlayers = hashMapOf<Int, Player>()

    internal val gpiLocalIndexes = IntArray(2048)

    internal var gpiLocalCount = 0

    internal val gpiExternalIndexes = IntArray(2048)

    internal var gpiExternalCount = 0

    internal val gpiInactivityFlags = IntArray(2048)

    internal val gpiTileHashMultipliers = IntArray(2048)

    /**
     * Send Packets
     */
    open fun handleMessages() {}

    open fun write(vararg messages: Message) {}

    open fun write(vararg messages: Any) {}

    open fun channelFlush() {}

    open fun channelClose() {}
}