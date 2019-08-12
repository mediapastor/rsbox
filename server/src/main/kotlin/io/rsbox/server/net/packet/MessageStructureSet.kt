package io.rsbox.server.net.packet

import io.rsbox.server.net.packet.builder.PacketDef
import io.rsbox.server.net.packet.builder.PacketDirection
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import mu.KLogging
import org.reflections.Reflections
import kotlin.reflect.full.companionObjectInstance

/**
 * @author Kyle Escobar
 */

class MessageStructureSet {
    private val structureClasses = Object2ObjectOpenHashMap<Class<*>, MessageStructure>()

    private val structureOpcodes = hashMapOf<Int, MessageStructure>()

    fun get(type: Class<*>): MessageStructure? = structureClasses[type]

    fun get(opcode: Int): MessageStructure? = structureOpcodes[opcode]

    @Suppress("UNCHECKED_CAST")
    fun load(encoders: MessageEncoderSet, decoders: MessageDecoderSet) {
        val reflections = Reflections("io.rsbox.server")
        val classes = reflections.getSubTypesOf(Message::class.java)

        classes.forEach { clazz ->
            val msg = (clazz.kotlin.companionObjectInstance as PacketDef)
            if(!msg.structure.packet.ignore) {
                structureClasses[clazz] = msg.structure.packet

                if(msg.direction == PacketDirection.INGRESS) {
                    structureOpcodes[msg.structure.packet.opcode] = msg.structure.packet

                    decoders.register(
                        clazz,
                        msg.coder as Class<MessageDecoder<Message>>,
                        msg.handler as Class<MessageHandler<Message>>
                    )
                } else {
                    encoders.register(clazz, msg.coder as Class<MessageEncoder<Message>>)
                }
            }
        }

        logger.info { "Loaded ${classes.size} total game packets." }
        logger.info { "Loaded ${decoders.count()} total game packet decoders." }
        logger.info { "Loaded ${encoders.count()} total game packet encoders." }
        logger.info { "Loaded ${decoders.countHandlers()} total game packet handlers." }
    }

    companion object : KLogging()
}