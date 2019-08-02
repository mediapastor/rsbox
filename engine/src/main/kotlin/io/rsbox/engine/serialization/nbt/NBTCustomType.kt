package io.rsbox.engine.serialization.nbt

/**
 * @author Kyle Escobar
 */

interface NBTCustomType {
    fun toNBT(): NBT

    fun fromNBT(data: NBT)
}