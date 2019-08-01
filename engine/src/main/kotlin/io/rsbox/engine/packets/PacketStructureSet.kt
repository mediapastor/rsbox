package io.rsbox.engine.packets

import io.rsbox.api.net.packet.Packet
import io.rsbox.api.net.packet.PacketStructure
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import mu.KotlinLogging
import org.reflections.Reflections
import kotlin.reflect.full.companionObjectInstance

/**
 * @author Kyle Escobar
 */

class PacketStructureSet {

    private val structureClasses = Object2ObjectOpenHashMap<Class<*>, PacketStructure>()

    private val structureOpcodes = arrayOfNulls<PacketStructure>(256)

    fun get(type: Class<*>): PacketStructure? = structureClasses[type]

    fun get(opcode: Int): PacketStructure? = structureOpcodes[opcode]


    fun load() {
        val reflections = Reflections("io.rsbox.engine")
        val classes = reflections.getSubTypesOf(Packet::class.java)

        classes.forEach { clazz ->
            val comp = clazz.kotlin.companionObjectInstance as PacketDef
            val out = comp.def

            if(!out.structure.ignore) {
                structureClasses[clazz] = out.structure
                if(out.direction == PacketDirection.IN) {
                    out.structure.opcodes.forEach { opcode -> structureOpcodes[opcode] = out.structure }
                }
            }
        }

        logger.info { "Loaded ${classes.size} total packet structures." }
    }

    companion object {
        val logger = KotlinLogging.logger {}
    }
}