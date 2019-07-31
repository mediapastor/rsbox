package io.rsbox.engine.model.entity

import io.rsbox.api.serialization.nbt.NBTSerializable
import io.rsbox.api.serialization.nbt.NBTTag

/**
 * @author Kyle Escobar
 */

open class Player : NBTSerializable() {
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
}