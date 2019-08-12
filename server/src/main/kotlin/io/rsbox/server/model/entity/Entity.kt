package io.rsbox.server.model.entity

import io.rsbox.api.world.Tile

/**
 * @author Kyle Escobar
 */

open class Entity : io.rsbox.api.entity.Entity {
    lateinit var tile: Tile
}