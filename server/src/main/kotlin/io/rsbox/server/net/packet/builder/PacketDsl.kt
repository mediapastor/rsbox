package io.rsbox.server.net.packet.builder

import io.rsbox.server.net.packet.MessageStructure
import io.rsbox.server.net.packet.MessageValue
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap
import org.bouncycastle.util.Pack

/**
 * @author Kyle Escobar
 */

data class PacketOutput(
    val packet: MessageStructure,
    val coder: Class<*>,
    val handler: Class<*>? = null,
    val direction: PacketDirection
)

enum class PacketDirection {
    INGRESS,
    EGRESS
}

interface PacketDef {
    val coder: Class<*>?
    val handler: Class<*>?
    val direction: PacketDirection
    val structure: PacketOutput
}

fun PacketDef.packet(init: PacketDsl.Builder.() -> Unit): PacketOutput {
    val builder = PacketDsl.Builder()
    init(builder)

    val p = builder.build()

    return PacketOutput(
        packet = p,
        coder = coder!!,
        handler = handler,
        direction = direction
    )
}

object PacketDsl {
    @DslMarker
    annotation class PacketDslMarker

    @PacketDslMarker
    class Builder {
        private var type: PacketType = PacketType.FIXED
        private var opcode: Int = -1
        private var length: Int = 0
        private var ignore: Boolean = false
        private var values: Object2ObjectLinkedOpenHashMap<String, MessageValue> = Object2ObjectLinkedOpenHashMap()

        fun build(): MessageStructure {
            return MessageStructure(type, opcode, length, ignore, values)
        }

        fun message(init: MessageBuilder.() -> Unit) {
            val builder = MessageBuilder()
            init(builder)

            type = builder.type
            opcode = builder.opcode
            length = builder.length
            ignore = builder.ignore
        }

        fun frames(vararg init: FrameBuilder.() -> Unit) {
            init.forEach { frame ->
                val builder = FrameBuilder()
                frame(builder)

                val pf = MessageValue(
                    id = builder.name,
                    order = builder.order,
                    trans = builder.trans,
                    type = builder.type,
                    sign = builder.sign
                )

                values[builder.name] = pf
            }
        }
    }

    @PacketDslMarker
    class MessageBuilder {
        var type: PacketType = PacketType.FIXED
        var opcode: Int = -1
        var length: Int = 0
        var ignore: Boolean = false
        var direction: PacketDirection = PacketDirection.EGRESS
    }

    @PacketDslMarker
    class FrameBuilder {
        var name: String = ""
        var type: DataType = DataType.BYTE
        var order: DataOrder = DataOrder.BIG
        var trans: DataTransformation = DataTransformation.NONE
        var sign: DataSignature = DataSignature.SIGNED
    }
}


