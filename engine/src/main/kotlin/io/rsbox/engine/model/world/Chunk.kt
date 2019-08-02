package io.rsbox.engine.model.world

/**
 * @author Kyle Escobar
 */

class Chunk(val coords: ChunkCoords, val heights: Int) {

    companion object {
        const val CHUNK_SIZE = 8

        const val CHUNKS_PER_REGION = 13

        const val CHUNK_VIEW_RADIUS = 3

        const val REGION_SIZE = CHUNK_SIZE * CHUNK_SIZE

        const val MAX_VIEWPORT = CHUNK_SIZE * CHUNKS_PER_REGION
    }
}