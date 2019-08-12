package io.rsbox.server.model.entity

import io.netty.channel.Channel
import io.rsbox.api.EventManager
import io.rsbox.api.event.PlayerLoadEvent
import io.rsbox.api.event.PlayerLoginEvent
import io.rsbox.server.ServerConstants
import io.rsbox.server.model.world.World
import io.rsbox.server.net.login.LoginRequest
import io.rsbox.server.net.packet.Message
import io.rsbox.server.net.packet.impl.message.RebuildLoginMessage
import io.rsbox.server.net.protocol.impl.GameProtocol
import io.rsbox.server.serializer.player.PlayerSave

/**
 * @author Kyle Escobar
 */

class Client(val channel: Channel) : Player(), io.rsbox.api.entity.Client {
    init {
        this.client = this
    }

    var clientResizable = false

    var clientWidth = 0

    var clientHeight = 0

    lateinit var gameProtocol: GameProtocol

    fun register() {
        this.world.register(this)
    }

    fun init(request: LoginRequest): Client {
        PlayerSave.load(this, request)
        this.register()
        return this
    }

    fun login() {
        gpiLocalPlayers[index] = this
        gpiLocalIndexes[gpiLocalCount++] = index

        for(i in 1 until 2048) {
            if(i == index) continue

            gpiExternalIndexes[gpiExternalCount++] = i
            gpiTileHashMultipliers[i] = if(world.players[i] != null) (world.players[i] as Player).tile.asTileHashMultiplier else 0
        }

        val tiles = IntArray(gpiTileHashMultipliers.size)
        System.arraycopy(gpiTileHashMultipliers, 0, tiles, 0, tiles.size)
        write(RebuildLoginMessage(index, tile, tiles, (world as World).xteaKeyService))

        EventManager.trigger(PlayerLoadEvent(this))
    }

    override fun handleMessages() {
        gameProtocol.handleMessages()
    }

    override fun write(vararg messages: Message) {
        messages.forEach { m -> gameProtocol.write(m) }
    }

    override fun write(vararg messages: Any) {
        messages.forEach { m -> channel.write(m) }
    }

    override fun channelFlush() {
        gameProtocol.flush()
    }

    override fun channelClose() {
        gameProtocol.close()
    }
}