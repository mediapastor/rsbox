package io.rsbox.engine.model.entity

import io.netty.channel.Channel
import io.rsbox.engine.model.storage.nbt.NBT

/**
 * @author Kyle Escobar
 */

class Client(val channel: Channel) : Player() {
    var clientWidth: Int = 0

    var clientHeight: Int = 0

    var clientFocus: Boolean = true

    override fun toNBT(): NBT {
        val data = super.toNBT()
        return data
    }
}