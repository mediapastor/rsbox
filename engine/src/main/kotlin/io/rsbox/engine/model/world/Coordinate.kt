package io.rsbox.engine.model.world

/**
 * @author Kyle Escobar
 */

class Coordinate(val x: Int, val z: Int, val height: Int) {
    constructor(tile: Tile) : this(tile.x, tile.z, tile.height)
    constructor(coordinate: Int) : this((coordinate and 0x7FFF), (coordinate shr 15) and 0x7FFF, coordinate ushr 30)

    val tile: Tile get() = Tile(x, z, height)
    val coordinate: Int get() = ((x and 0x7FFF) or ((z and 0x7FFF) shl 15) or (height shl 30))
}