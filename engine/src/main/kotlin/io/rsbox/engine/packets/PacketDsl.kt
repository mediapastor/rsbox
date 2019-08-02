package io.rsbox.engine.packets

import io.rsbox.engine.net.packet.*
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap

/**
 * @author Kyle Escobar
 */

interface PacketDef {
    val def: PacketBuilderOutput
}

fun PacketDef.packet(direction: PacketDirection,  init: PacketDsl.Builder.() -> Unit): PacketBuilderOutput {
    val builder = PacketDsl.Builder()
    init(builder)

    return PacketBuilderOutput(direction, builder.build())
}

object PacketDsl {
    @DslMarker
    annotation class PacketDslMarker

    @PacketDslMarker
    class Builder {
        private var type: PacketType = PacketType.FIXED
        private var opcodes: IntArray = intArrayOf(-1)
        private var length: Int = 0
        private var ignore: Boolean = false
        private var values: Object2ObjectLinkedOpenHashMap<String, PacketValue> = Object2ObjectLinkedOpenHashMap()

        fun build(): PacketStructure {
            if(values.size == 0) {
                values = Object2ObjectLinkedOpenHashMap(0)
            }

            return PacketStructure(type, opcodes, length, ignore, values)
        }

        fun message(init: MessageBuilder.() -> Unit) {
            val builder = MessageBuilder()
            init(builder)

            type = builder.type
            opcodes = if(builder.opcodes.isNotEmpty()) builder.opcodes else intArrayOf(builder.opcode)
            length = builder.length
            ignore = builder.ignore
        }

        fun structure(vararg init: StructureBuilder.() -> Unit) {
            init.forEach { frame ->
                val builder = StructureBuilder()
                frame(builder)

                val pv = PacketValue(
                    id = builder.name,
                    order = builder.order,
                    transformation = builder.transformation,
                    type = builder.type,
                    signature = builder.signature
                )

                values[builder.name] = pv
            }
        }
    }

    @PacketDslMarker
    class MessageBuilder {
        var type: PacketType = PacketType.FIXED
        var opcodes: IntArray = intArrayOf()
        var opcode: Int = -1
        var length: Int = 0
        var ignore: Boolean = false
    }

    @PacketDslMarker
    class StructureBuilder {
        var name: String = ""
        var type: DataType = DataType.BYTE
        var order: DataOrder = DataOrder.BIG
        var transformation: DataTransformation = DataTransformation.NONE
        var signature: DataSignature = DataSignature.SIGNED
    }
}

enum class PacketDirection {
    IN,
    OUT
}

data class PacketBuilderOutput(val direction: PacketDirection, val structure: PacketStructure)