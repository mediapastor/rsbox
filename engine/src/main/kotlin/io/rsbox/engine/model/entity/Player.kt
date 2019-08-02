package io.rsbox.engine.model.entity

import io.rsbox.engine.Launcher
import io.rsbox.engine.Server
import io.rsbox.engine.net.packet.Packet
import io.rsbox.engine.serialization.nbt.NBTTag
import io.rsbox.engine.model.world.RSWorld
import io.rsbox.engine.model.world.Tile
import io.rsbox.engine.packets.impl.PacketOutRebuildLogin
import it.unimi.dsi.fastutil.objects.ObjectArrayList

/**
 * @author Kyle Escobar
 */

open class Player : LivingEntity() {

    var initiated = false

    val server: Server = Launcher.server

    @NBTTag("display_name")
    lateinit var displayName: String

    @NBTTag("uuid")
    lateinit var uuid: String

    @NBTTag("current_xteas")
    lateinit var currentXteas: IntArray

    @NBTTag("privilege")
    var privilege: Int = 0

    /**
     * The cached world instance stored on the player object
     */
    val world: RSWorld = server.world

    var lastIndex: Int = -1

    val gpiLocalPlayers = arrayOfNulls<Player>(2048)
    val gpiLocalIndexes = IntArray(2048)
    var gpiLocalCount = 0
    val gpiExternalIndexes = IntArray(2048)
    var gpiExternalCount = 0
    val gpiInactivityFlags = IntArray(2048)
    val gpiTileHashMultipliers = IntArray(2048)
    val localNpcs = ObjectArrayList<Npc>()

    fun register() {
        world.register(this)
    }

    fun login() {
        gpiLocalPlayers[index] = this
        gpiLocalIndexes[gpiLocalCount++] = index

        for(i in 1 until 2048) {
            if(i == index) continue

            gpiExternalIndexes[gpiExternalCount++] = i
            gpiTileHashMultipliers[i] = if(i < world.players.capacity) world.players[i]?.tile?.asClientEncryptedHash ?: 0 else 0
        }

        val tiles = IntArray(gpiTileHashMultipliers.size)
        System.arraycopy(gpiTileHashMultipliers, 0, tiles, 0, tiles.size)

        sendPacket(PacketOutRebuildLogin(index, tile, tiles, world.xteaKeyService!!))

        Server.logger.info { "Login request accepted for [uuid=${uuid}]. Login request removed from the queue with [status=success]." }

        initiated = true
    }


    /**
     * Network related Methods
     */

    open fun handleIngressPackets() {}
    open fun sendPacket(vararg packets: Packet) {}
}