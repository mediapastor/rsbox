package io.rsbox.engine.model.world

import io.rsbox.engine.serialization.nbt.NBT
import io.rsbox.engine.serialization.nbt.NBTCustomType
import io.rsbox.engine.serialization.nbt.nbt

/**
 * @author Kyle Escobar
 */

class Tile : NBTCustomType {

    var x: Int = 0
    var z: Int = 0
    var height: Int = 0

    val topLeftRegionX: Int get() = (x shr 3) - 6
    val topLeftRegionZ: Int get() = (z shr 3) - 6

    val regionId: Int get() = ((x shr 6) shl 8) or (z shr 6)

    /**
     * Constructors
     */

    constructor(x: Int, z: Int, height: Int = 0) {
        this.x = x
        this.z = z
        this.height = height
    }

    constructor(other: Tile) : this(other.x, other.z, other.height)

    constructor()


    /**
     * The tile encoded for packets
     */
    val asPacketInteger: Int get() = (z and 0x3FFF) or ((x and 0x3FFF) shl 14) or ((height and 0x3) shl 28)

    /**
     * The tile encoded for decrypting by client
     * xtea decryption keys
     */
    val asClientEncryptedHash: Int get() = (z shr 13) or ((x shr 13) shl 8) or ((height and 0x3) shl 16)

    override fun toNBT(): NBT {
        val data = nbt()
        data.int.set("x", this.x)
        data.int.set("z", this.z)
        data.int.set("height", this.height)
        return data
    }

    override fun fromNBT(data: NBT) {
        x = data.int.get("x") ?: 0
        z = data.int.get("z") ?: 0
        height = data.int.get("height") ?: 0
    }
}