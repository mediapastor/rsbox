package io.rsbox.engine.sync.task

import io.rsbox.engine.model.entity.Player
import io.rsbox.engine.model.world.Chunk
import io.rsbox.engine.model.world.Coordinate
import io.rsbox.engine.model.world.Tile
import io.rsbox.engine.packets.impl.PacketOutRebuildNormal
import io.rsbox.engine.sync.SyncTask

/**
 * @author Kyle Escobar
 */

object PlayerPreSyncTask : SyncTask<Player> {
    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun run(player: Player) {
        val last = player.lastRegionBase
        val current = player.tile

        if(last == null || rebuildRegionCheck(last, current)) {
            val regionX = ((current.x shr 3) - (Chunk.MAX_VIEWPORT shr 4)) shl 3
            val regionZ = ((current.z shr 3) - (Chunk.MAX_VIEWPORT shr 4)) shl 3
            player.lastRegionBase = Coordinate(regionX, regionZ, current.height)

            val xteaService = player.world.xteaKeyService

            // Instance stuff should be here.

            val rebuildPacket = PacketOutRebuildNormal(current.x shr 3, current.z shr 3, xteaService!!)
            player.sendPacket(rebuildPacket)
        }
    }

    private fun rebuildRegionCheck(old: Coordinate, new: Tile): Boolean {
        val dx = new.x - old.x
        val dz = new.z - old.z
        return dx <= Player.NORMAL_VIEW_DISTANCE || dx >= Chunk.MAX_VIEWPORT - Player.NORMAL_VIEW_DISTANCE - 1
                || dz <= Player.NORMAL_VIEW_DISTANCE || dz >= Chunk.MAX_VIEWPORT - Player.NORMAL_VIEW_DISTANCE - 1
    }
}