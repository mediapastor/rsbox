package io.rsbox.server.service.impl

import com.google.gson.Gson
import io.rsbox.server.Launcher
import io.rsbox.server.ServerConstants
import io.rsbox.server.model.world.World
import io.rsbox.server.service.Service
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import mu.KLogging
import net.runelite.cache.IndexType
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Files

/**
 * @author Kyle Escobar
 */

class XteaKeyService : Service() {

    private val keys = Int2ObjectOpenHashMap<IntArray>()

    val validRegions: IntArray
        get() = keys.keys.toIntArray()

    val world: World = Launcher.server.world as World

    init {
        world.xteaKeyService = this
    }

    override fun onStart() {
        val file = File(ServerConstants.XTEAS_PATH)
        if(!file.exists()) {
            throw FileNotFoundException("Xteas file does not exist.")
        }

        loadFile(file)

        loadKeys(world)
    }

    override fun onStop() {

    }

    private fun loadKeys(world: World) {
        val maxRegions = Short.MAX_VALUE
        var totalRegions = 0
        val missingKeys = mutableListOf<Int>()

        val regionIndex = world.server.cacheStore.getIndex(IndexType.MAPS)
        for(regionId in 0 until maxRegions) {
            val x = regionId shr 8
            val z = regionId and 0xFF

            regionIndex.findArchiveByName("m${x}_$z") ?: continue
            regionIndex.findArchiveByName("l${x}_$z") ?: continue

            totalRegions++

            if(get(regionId).contentEquals(EMPTY_KEYS)) {
                missingKeys.add(regionId)
            }
        }

        val validKeys = totalRegions - missingKeys.size
        logger.info("Loaded {} / {} ({}%) XTEA keys.", validKeys, totalRegions,
            String.format("%.2f", (validKeys.toDouble() * 100.0) / totalRegions.toDouble()))
    }

    fun get(region: Int): IntArray {
        if(keys[region] == null) {
            logger.trace { "No XTEA keys found for region $region." }
            keys[region] = EMPTY_KEYS
        }
        return keys[region]!!
    }

    private fun loadFile(file: File) {
        val reader = Files.newBufferedReader(file.toPath())
        val xteas = Gson().fromJson(reader, Array<XteaFile>::class.java)
        reader.close()
        xteas?.forEach { xtea ->
            keys[xtea.region] = xtea.keys
        }
    }

    private data class XteaFile(val region: Int, val keys: IntArray) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as XteaFile

            if (region != other.region) return false
            if (!keys.contentEquals(other.keys)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = region
            result = 31 * result + keys.contentHashCode()
            return result
        }
    }

    companion object : KLogging() {
        val EMPTY_KEYS = intArrayOf(0,0,0,0)
    }
}