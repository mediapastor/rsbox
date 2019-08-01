package io.rsbox.engine.model.entity

import io.rsbox.api.RSBox
import io.rsbox.api.serialization.nbt.NBTTag
import io.rsbox.engine.model.RSWorld
import it.unimi.dsi.fastutil.objects.ObjectArrayList

/**
 * @author Kyle Escobar
 */

open class Player : LivingEntity() {

    var initiated = false

    @NBTTag("username")
    lateinit var username: String

    @NBTTag("display_name")
    lateinit var displayName: String

    @NBTTag("uuid")
    lateinit var uuid: String

    @NBTTag("password")
    lateinit var password: String

    @NBTTag("current_xteas")
    lateinit var currentXteas: List<Int>

    @NBTTag("privilege")
    var privilege: Int = 0

    val world: RSWorld = RSBox.server.world as RSWorld

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
        }
    }
}