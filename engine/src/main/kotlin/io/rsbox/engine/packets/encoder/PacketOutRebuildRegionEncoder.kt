package io.rsbox.engine.packets.encoder

import io.netty.buffer.Unpooled
import io.rsbox.engine.net.packet.GamePacketBuilder
import io.rsbox.engine.net.packet.PacketEncoder
import io.rsbox.engine.packets.impl.PacketOutRebuildRegion

/**
 * @author Kyle Escobar
 */

class PacketOutRebuildRegionEncoder : PacketEncoder<PacketOutRebuildRegion>() {
    override fun extract(message: PacketOutRebuildRegion, key: String): Number = when(key) {
        "x" -> message.x
        "z" -> message.z
        "force_load" -> message.forceLoad
        else -> throw Exception("Unhandled key value.")
    }

    override fun extractBytes(message: PacketOutRebuildRegion, key: String): ByteArray = when(key) {
        "data" -> {
            val xteaBuffer = Unpooled.buffer(message.coordinates.size * (Int.SIZE_BYTES * 4))
            val regions = hashSetOf<Int>()
            var xteaCount = 0
            message.coordinates.forEach { ccoord ->
                if(ccoord == -1) return@forEach
                val rx = ccoord shr 14 and 0x3FF
                val rz = ccoord shr 3 and 0x7FF
                val region = rz / 8 + (rx / 8 shl 8)
                if(regions.add(region)) {
                    val keys = message.xteaKeyService.get(region)
                    for(xteaKey in keys) {
                        xteaBuffer.writeInt(xteaKey)
                    }
                    xteaCount++
                }
            }

            val bitBuf = GamePacketBuilder()
            bitBuf.switchToBitAccess()
            var index = 0
            message.coordinates.forEach { ccoord ->
                bitBuf.putBit(ccoord != -1)
                if(ccoord != -1) {
                    bitBuf.putBits(26, ccoord)
                }
                index++
            }
            bitBuf.switchToByteAccess()

            val buf = Unpooled.buffer(Short.SIZE_BYTES + bitBuf.readableBytes + xteaBuffer.readableBytes())

            buf.writeShort(xteaCount)
            buf.writeBytes(bitBuf.byteBuf)
            buf.writeBytes(xteaBuffer)

            val data = ByteArray(buf.readableBytes())
            buf.readBytes(data)
            data
        }
        else -> throw Exception("Unhandled key value.")
    }
}