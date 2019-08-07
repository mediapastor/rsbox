package io.rsbox.net.protocol.impl

import com.google.common.primitives.Ints
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.rsbox.net.Network
import io.rsbox.net.js5.JS5Request
import io.rsbox.net.js5.JS5Response
import io.rsbox.net.protocol.GameProtocol
import net.runelite.cache.fs.Container
import net.runelite.cache.fs.jagex.CompressionType
import net.runelite.cache.fs.jagex.DiskStorage

/**
 * @author Kyle Escobar
 */

class JS5Protocol(channel: Channel) : GameProtocol(channel) {
    private val cacheStore = Network.cacheStore

    override fun receiveMessage(ctx: ChannelHandlerContext, msg: Any) {
        if(msg is JS5Request) {
            if(msg.index == 255) {
                encodeIndexData(ctx,msg)
            } else {
                encodeFileData(ctx,msg)
            }
        }
    }

    override fun terminate() {}

    private fun encodeIndexData(ctx: ChannelHandlerContext, req: JS5Request) {
        val data: ByteArray

        if(req.archive == 255) {
            if(cacheIndex == null) {
                val buf = ctx.alloc().heapBuffer(cacheStore.indexes.size * 8)

                cacheStore.indexes.forEach { index ->
                    buf.writeInt(index.crc)
                    buf.writeInt(index.revision)
                }

                val container = Container(CompressionType.NONE, -1)
                container.compress(buf.array().copyOf(buf.readableBytes()), null)
                cacheIndex = container.data
                buf.release()
            }
            data = cacheIndex!!
        } else {
            val storage = cacheStore.storage as DiskStorage
            data = storage.readIndex(req.archive)
        }

        val response = JS5Response(req.index, req.archive, data)
        ctx.writeAndFlush(response)
    }

    private fun encodeFileData(ctx: ChannelHandlerContext, req: JS5Request) {
        val index = cacheStore.findIndex(req.index)!!
        val archive = index.getArchive(req.archive)!!
        var data = cacheStore.storage.loadArchive(archive)

        if(data != null) {
            val compression = data[0]
            val length = Ints.fromBytes(data[1], data[2], data[3], data[4])
            val expectedLengths = length + (if (compression.toInt() != CompressionType.NONE) 9 else 5)
            if(expectedLengths != length && data.size - expectedLengths == 2) {
                data = data.copyOf(data.size - 2)
            }

            val response = JS5Response(req.index, req.archive, data)
            ctx.writeAndFlush(response)
        }
    }

    companion object {
        private var cacheIndex: ByteArray? = null
    }
}