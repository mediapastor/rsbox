package io.rsbox.server.net.login

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.rsbox.server.Launcher
import io.rsbox.api.ServerResultType
import io.rsbox.server.net.StatefulFrameDecoder
import io.rsbox.server.net.rsa.RSA
import mu.KLogging
import java.math.BigInteger
import io.rsbox.server.util.BufferUtils.readString
import io.rsbox.server.util.BufferUtils.readJagexString
import io.rsbox.server.util.Xtea

/**
 * @author Kyle Escobar
 */

class LoginDecoder(private val serverSeed: Long) : StatefulFrameDecoder<LoginDecoderState>(LoginDecoderState.HANDSHAKE) {

    var payloadLength = -1
    var reconnecting = false

    override fun decode(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>, state: LoginDecoderState) {
        buf.markReaderIndex()
        when(state) {
            LoginDecoderState.HANDSHAKE -> decodeHandshake(ctx,buf)
            LoginDecoderState.HEADER -> decodeHeader(ctx,buf,out)
        }
    }

    private fun decodeHandshake(ctx: ChannelHandlerContext, buf: ByteBuf) {
        if(buf.isReadable) {
            val opcode = buf.readByte().toInt()
            if(opcode == 16 || opcode == 18) {
                reconnecting = opcode == 18
                setState(LoginDecoderState.HEADER)
            } else {
                ctx.writeServerResult(ServerResultType.BAD_SESSION_ID)
            }
        }
    }

    private fun decodeHeader(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>) {
        if(buf.readableBytes() >= 3) {
            val size = buf.readUnsignedShort()
            if(buf.readableBytes() >= size) {
                val revision = buf.readInt()

                buf.skipBytes(Int.SIZE_BYTES)
                buf.skipBytes(Byte.SIZE_BYTES)

                if(revision == Launcher.server.revision) {
                    payloadLength = size - (Int.SIZE_BYTES + Int.SIZE_BYTES + Byte.SIZE_BYTES)
                    decodePayload(ctx,buf,out)
                } else {
                    ctx.writeServerResult(ServerResultType.REVISION_MISMATCH)
                }
            } else {
                buf.resetReaderIndex()
            }
        }
    }

    private fun decodePayload(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>) {
        if(buf.readableBytes() >= payloadLength) {
            buf.markReaderIndex()

            val rsaExponent = RSA.exponent
            val rsaModulus = RSA.modulus

            val secureBuf: ByteBuf = run {
                val secureBufLength = buf.readUnsignedShort()
                val secureBuf2 = buf.readBytes(secureBufLength)
                val rsaValue = BigInteger(secureBuf2.array()).modPow(rsaExponent, rsaModulus)
                Unpooled.wrappedBuffer(rsaValue.toByteArray())
            }

            val successfulEncryption = secureBuf.readUnsignedByte().toInt() == 1
            if(!successfulEncryption) {
                buf.resetReaderIndex()
                buf.skipBytes(payloadLength)
                logger.info { "Login request rejected. RSA Encryption key does not match on the client." }
                ctx.writeServerResult(ServerResultType.BAD_SESSION_ID)
                return
            }

            val xteaKeys = IntArray(4) { secureBuf.readInt() }
            val clientSeed = secureBuf.readLong()
            val authCode: Int
            val password: String?
            val previousXteaKeys = IntArray(4)

            if(reconnecting) {
                for(i in 0 until previousXteaKeys.size) {
                    previousXteaKeys[i] = secureBuf.readInt()
                }

                authCode = -1
                password = null
            } else {
                val authType = secureBuf.readByte().toInt()

                if(authType == 1) {
                    authCode = secureBuf.readInt()
                }
                else if(authType == 0 || authType == 2) {
                    authCode = secureBuf.readUnsignedMedium()
                    secureBuf.skipBytes(Byte.SIZE_BYTES)
                }
                else {
                    authCode = secureBuf.readInt()
                }

                secureBuf.skipBytes(Byte.SIZE_BYTES)
                password = secureBuf.readString()
            }

            val xteaBuf = buf.decipher(xteaKeys)
            val username = xteaBuf.readString()

            if(clientSeed != serverSeed) {
                xteaBuf.resetReaderIndex()
                xteaBuf.skipBytes(payloadLength)
                logger.info { "Login request rejected for user $username due to seed mismatch." }
                ctx.writeServerResult(ServerResultType.BAD_SESSION_ID)
                return
            }

            val clientSettings = xteaBuf.readByte().toInt()
            val clientResizable = (clientSettings shr 1) == 1
            val clientWidth = xteaBuf.readUnsignedShort()
            val clientHeight = xteaBuf.readUnsignedShort()

            xteaBuf.skipBytes(24) // random.dat data
            xteaBuf.readString()
            xteaBuf.skipBytes(Int.SIZE_BYTES)

            xteaBuf.skipBytes(Byte.SIZE_BYTES * 10)
            xteaBuf.skipBytes(Short.SIZE_BYTES)
            xteaBuf.skipBytes(Byte.SIZE_BYTES)
            xteaBuf.skipBytes(Byte.SIZE_BYTES * 3)
            xteaBuf.skipBytes(Short.SIZE_BYTES)
            xteaBuf.readJagexString()
            xteaBuf.readJagexString()
            xteaBuf.readJagexString()
            xteaBuf.readJagexString()
            xteaBuf.skipBytes(Byte.SIZE_BYTES)
            xteaBuf.skipBytes(Short.SIZE_BYTES)
            xteaBuf.readJagexString()
            xteaBuf.readJagexString()
            xteaBuf.skipBytes(Byte.SIZE_BYTES * 2)
            xteaBuf.skipBytes(Int.SIZE_BYTES * 3)
            xteaBuf.skipBytes(Int.SIZE_BYTES)
            xteaBuf.readJagexString()

            xteaBuf.skipBytes(Int.SIZE_BYTES * 3)

            val cacheCrcs = Launcher.server.cacheStore.indexes.map { it.crc }.toIntArray()
            val crcs = IntArray(cacheCrcs.size) { xteaBuf.readInt() }

            for(i in 0 until crcs.size) {
                if(i == 16) {
                    continue
                }
                if(crcs[i] != cacheCrcs[i]) {
                    buf.resetReaderIndex()
                    buf.skipBytes(payloadLength)
                    logger.info { "Login request for user $username rejected due to cache crc mismatch." }
                    ctx.writeServerResult(ServerResultType.REVISION_MISMATCH)
                    return
                }
            }

            val request = LoginRequest(
                channel = ctx.channel(),
                username = username,
                password = password ?: "",
                revision = Launcher.server.revision,
                xteaKeys = xteaKeys,
                resizableClient = clientResizable,
                clientWidth = clientWidth,
                clientHeight = clientHeight,
                authCode = authCode,
                uuid = "".toUpperCase(),
                reconnecting = reconnecting
            )

            out.add(request)
        }
    }

    private fun ChannelHandlerContext.writeServerResult(result: ServerResultType) {
        val buf = channel().alloc().buffer(1)
        buf.writeByte(result.id)
        writeAndFlush(buf).addListener { ChannelFutureListener.CLOSE }
    }

    private fun ByteBuf.decipher(xteaKeys: IntArray): ByteBuf {
        val data = ByteArray(readableBytes())
        readBytes(data)
        return Unpooled.wrappedBuffer(Xtea.decipher(xteaKeys, data, 0, data.size))
    }

    companion object : KLogging()
}

enum class LoginDecoderState {
    HANDSHAKE,
    HEADER
}