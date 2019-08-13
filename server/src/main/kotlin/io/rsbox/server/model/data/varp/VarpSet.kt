package io.rsbox.server.model.data.varp

import io.rsbox.server.util.BitManipulation
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet

/**
 * @author Kyle Escobar
 */

class VarpSet(val maxVarps: Int) {

    private val varps = mutableListOf<Varp>().apply {
        for(i in 0 until maxVarps) {
            add(Varp(id = i, state = 0))
        }
    }

    private val dirty = ShortOpenHashSet(maxVarps)

    operator fun get(id: Int): Varp = varps[id]

    fun getState(id: Int): Int = varps[id].state

    fun setState(id: Int, state: Int): VarpSet {
        varps[id].state = state
        dirty.add(id.toShort())
        return this
    }

    fun getBit(id: Int, startBit: Int, endBit: Int): Int = BitManipulation.getBit(getState(id), startBit, endBit)

    fun setBit(id: Int, startBit: Int, endBit: Int, value: Int): VarpSet {
        return setState(id, BitManipulation.setBit(getState(id), startBit, endBit, value))
    }

    fun isDirty(id: Int): Boolean = dirty.contains(id.toShort())

    fun clean() = dirty.clear()

    fun getAll(): List<Varp> = varps
}