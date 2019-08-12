package io.rsbox.server.def

import io.netty.buffer.Unpooled
import io.rsbox.server.def.impl.*
import io.rsbox.server.service.impl.XteaKeyService
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import mu.KLogging
import net.runelite.cache.ConfigType
import net.runelite.cache.IndexType
import net.runelite.cache.fs.Store
import java.io.FileNotFoundException

/**
 * @author Kyle Escobar
 */

class DefinitionSet {

    private val defs = Object2ObjectOpenHashMap<Class<out Definition>, Map<Int, *>>()

    fun loadAll(store: Store) {
        /*
 * Load [AnimDef]s.
 */
        load(store, AnimDef::class.java)
        logger.info("Loaded ${getCount(AnimDef::class.java)} animation definitions.")

        /*
         * Load [VarpDef]s.
         */
        load(store, VarpDef::class.java)
        logger.info("Loaded ${getCount(VarpDef::class.java)} varp definitions.")

        /*
         * Load [VarbitDef]s.
         */
        load(store, VarbitDef::class.java)
        logger.info("Loaded ${getCount(VarbitDef::class.java)} varbit definitions.")

        /*
         * Load [EnumDef]s.
         */
        load(store, EnumDef::class.java)
        logger.info("Loaded ${getCount(EnumDef::class.java)} enum definitions.")

        /*
         * Load [NpcDef]s.
         */
        load(store, NpcDef::class.java)
        logger.info("Loaded ${getCount(NpcDef::class.java)} npc definitions.")

        /*
         * Load [ItemDef]s.
         */
        load(store, ItemDef::class.java)
        logger.info("Loaded ${getCount(ItemDef::class.java)} item definitions.")

        /*
         * Load [ObjectDef]s.
         */
        load(store, ObjectDef::class.java)
        logger.info("Loaded ${getCount(ObjectDef::class.java)} object definitions.")
    }

    fun <T : Definition> load(store: Store, type: Class<out T>) {
        val configType: ConfigType = when(type) {
            VarpDef::class.java -> ConfigType.VARPLAYER
            VarbitDef::class.java -> ConfigType.VARBIT
            EnumDef::class.java -> ConfigType.ENUM
            NpcDef::class.java -> ConfigType.NPC
            ObjectDef::class.java -> ConfigType.OBJECT
            ItemDef::class.java -> ConfigType.ITEM
            AnimDef::class.java -> ConfigType.SEQUENCE
            else -> throw IllegalArgumentException("Unhandled class type ${type::class.java}.")
        }
        val configs = store.getIndex(IndexType.CONFIGS) ?: throw FileNotFoundException("Cache was not found in data folder.")
        val archive = configs.getArchive(configType.id)
        val files = archive.getFiles(store.storage.loadArchive(archive)!!).files

        val definitions = Int2ObjectOpenHashMap<T?>(files.size + 1)
        for(i in 0 until files.size) {
            val def = createDefinition(type, files[i].fileId, files[i].contents)
            definitions[files[i].fileId] = def
        }
        defs[type] = definitions
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Definition> createDefinition(type: Class<out T>, id: Int, data: ByteArray): T {
        val def: Definition = when(type) {
            VarpDef::class.java -> VarpDef(id)
            VarbitDef::class.java -> VarbitDef(id)
            EnumDef::class.java -> EnumDef(id)
            NpcDef::class.java -> NpcDef(id)
            ObjectDef::class.java -> ObjectDef(id)
            ItemDef::class.java -> ItemDef(id)
            AnimDef::class.java -> AnimDef(id)
            else -> throw IllegalArgumentException("Unhandled class type ${type::class.java}.")
        }

        val buf = Unpooled.wrappedBuffer(data)
        def.decode(buf)
        buf.release()
        return def as T
    }

    fun getCount(type: Class<*>) = defs[type]!!.size

    @Suppress("UNCHECKED_CAST")
    fun <T : Definition> get(type: Class<out T>, id: Int): T {
        return (defs[type]!!)[id] as T
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Definition> getNullable(type: Class<out T>, id: Int): T? {
        return (defs[type]!!)[id] as T?
    }

    companion object: KLogging()
}