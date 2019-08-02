package io.rsbox.engine.model.world

/**
 * @author Kyle Escobar
 */

class ChunkCoords(val x: Int, val z: Int) {
    fun toTile(): Tile = Tile((x + 6) shl 3, (z + 6) shl 3)
}