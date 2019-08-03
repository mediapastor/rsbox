package io.rsbox.engine.model.world

import io.rsbox.engine.serialization.nbt.NBT
import io.rsbox.engine.serialization.nbt.NBTCustomType
import io.rsbox.engine.serialization.nbt.nbt

/**
 * @author Kyle Escobar
 */

class Tile : NBTCustomType {
    private var coordinate: Int = 0
    var x: Int = coordinate and 0x7FFF
    var z: Int = (coordinate shr 15) and 0x7FFF
    var height: Int = coordinate ushr 30

    val topLeftRegionX: Int get() = (x shr 3) - 6
    val topLeftRegionZ: Int get() = (z shr 3) - 6

    val regionId: Int get() = ((x shr 6) shl 8) or (z shr 6)

    /**
     * Constructors
     */
    private constructor(coordinate: Int) {
        this.coordinate = coordinate
    }

    constructor(x: Int, z: Int, height: Int = 0) : this((x and 0x7FFF) or ((z and 0x7FFF) shl 15) or (height shl 30))

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

    override fun hashCode(): Int = coordinate

    override fun equals(other: Any?): Boolean {
        if(other is Tile) {
            return other.coordinate == coordinate
        }
        return false
    }
}