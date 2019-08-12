package io.rsbox.server.model.entity

import io.rsbox.server.Launcher
import io.rsbox.server.Server
import io.rsbox.server.model.world.World
import io.rsbox.server.net.packet.Message

/**
 * @author Kyle Escobar
 */

open class Player : LivingEntity(), io.rsbox.api.entity.Player {
    override var username = ""

    override var displayName = ""

    override var password = ""

    override var uuid = ""

    override var privilege = 0

    lateinit var client: Client

    var lastIndex = -1

    override var world: io.rsbox.api.World = Launcher.server.world

    override var server: io.rsbox.api.Server = Launcher.server

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