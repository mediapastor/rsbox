package io.rsbox.engine.model.entity

import io.rsbox.engine.model.storage.nbt.NBT
import io.rsbox.engine.model.storage.nbt.nbt

/**
 * @author Kyle Escobar
 */

open class Player {

    lateinit var username: String

    lateinit var displayName: String

    lateinit var password: String

    lateinit var uuid: String

    lateinit var currentXteas: IntArray

    var privilege: Int = 0

    var index: Int = 0

    open fun toNBT(): NBT {
        val data = nbt()
        data.string.set("username",username)
        data.string.set("display_name", displayName)
        data.string.set("password", password)
        data.string.set("uuid", uuid)
        data.ints.set("current_xteas", currentXteas.toList())
        data.int.set("privilege", privilege)
        return data
    }
}