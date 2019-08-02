package io.rsbox.engine.packets.encoder

import io.netty.buffer.Unpooled
import io.rsbox.engine.net.packet.GamePacketBuilder
import io.rsbox.engine.net.packet.PacketEncoder
import io.rsbox.engine.model.world.Chunk
import io.rsbox.engine.packets.impl.PacketOutRebuildLogin

/**
 * @author Kyle Escobar
 */

class PacketOutRebuildLoginEncoder : PacketEncoder<PacketOutRebuildLogin>() {
    override fun extract(message: PacketOutRebuildLogin, key: String): Number = when(key) {
        "x" -> message.tile.x shr 3
        "z" -> message.tile.z shr 3
        else -> throw Exception("Unhandled key value.")
    }

    override fun extractBytes(message: PacketOutRebuildLogin, key: String): ByteArray = when(key) {
        "gpi" -> {
            val buf = GamePacketBuilder()

            buf.switchToBitAccess()
            buf.putBits(30, message.tile.asPacketInteger)
            for(i in 1 until 2048) {
                if(i != message.playerIndex) {
                    buf.putBits(18, message.playerTiles[i])
                }
            }
            buf.switchToByteAccess()

            val gpi = ByteArray(buf.byteBuf.readableBytes())
            buf.byteBuf.readBytes(gpi)

            gpi
        }

        "xteas" -> {
            val chunkX = (message.tile.x shr 3)
            val chunkZ = (message.tile.z shr 3)

            val lx = (chunkX - (Chunk.MAX_VIEWPORT shr 4)) shr 3
            val rx = (chunkX + (Chunk.MAX_VIEWPORT shr 4)) shr 3
            val lz = (chunkZ - (Chunk.MAX_VIEWPORT shr 4)) shr 3
            val rz = (chunkZ + (Chunk.MAX_VIEWPORT shr 4)) shr 3

            val buf = Unpooled.buffer(Short.SIZE_BYTES + (Int.SIZE_BYTES * 10))
            var forceSend = false
            if((chunkX / 8 == 48 || chunkX / 8 == 49) && chunkZ / 8 == 48) {
                forceSend = true
            }
            if(chunkX / 8 == 48 && chunkZ / 8 == 148) {
                forceSend = true
            }

            var count = 0
            buf.writeShort(count)
            for(x in lx..rx) {
                for(z in lz..rz) {
                    if(!forceSend || z != 49 && z != 149 && z != 147 && x != 50 && (x != 49 || z != 47)) {
                        val region = z + (x shl 8)
                        val keys = message.xteaKeyService.get(region)
                        for(xteaKey in keys) {
                            buf.writeInt(xteaKey)
                        }
                        count++
                    }
                }
            }
            buf.setShort(0, count)

            val xteas = ByteArray(buf.readableBytes())
            buf.readBytes(xteas)
            xteas
        }

        else -> throw Exception("Unhandled key value.")
    }
}