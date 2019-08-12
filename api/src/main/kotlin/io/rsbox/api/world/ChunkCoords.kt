package io.rsbox.api.world

/**
 * @author Kyle Escobar
 */

class ChunkCoords(val x: Int, val z: Int) {
    fun toTile(): Tile = Tile((x + 6) shl 3, (z + 6) shl 3)

    companion object {
        fun fromTile(x: Int, z: Int): ChunkCoords = ChunkCoords(x, z)

        fun fromTile(tile: Tile): ChunkCoords =
            fromTile(tile.topLeftRegionX, tile.topLeftRegionZ)
    }
}