package io.rsbox.engine.model.entity

import io.rsbox.engine.model.world.Tile
import io.rsbox.engine.serialization.nbt.NBTSerializable
import io.rsbox.engine.serialization.nbt.NBTTag

/**
 * @author Kyle Escobar
 */

open class Entity : NBTSerializable {

    @NBTTag("tile")
    var tile: Tile = Tile(0,0,0)

}