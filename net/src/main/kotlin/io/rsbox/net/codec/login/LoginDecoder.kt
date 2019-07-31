package io.rsbox.net.codec.login

import io.netty.buffer.ByteBuf
import io.rsbox.util.BufferUtils.readJagexString
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.rsbox.api.net.login.LoginRequest
import io.rsbox.net.codec.StatefulFrameDecoder
import mu.KotlinLogging
import java.math.BigInteger
import io.rsbox.util.BufferUtils.readString
import io.rsbox.util.Xtea

/**
 * @author Kyle Escobar
 */

class LoginDecoder(
    private val revision: Int,
    private val crcs: IntArray,
    private val serverSeed: Long,
    private val rsaExponent: BigInteger,
    private val rsaModulus: BigInteger
) : StatefulFrameDecoder<LoginDecoderState>(LoginDecoderState.HANDSHAKE) {

    private var payloadLength = -1
    private var reconnecting = false

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
            if(opcode == LOGIN_OPCODE || opcode == RECONNECT_OPCODE) {
                reconnecting = opcode == RECONNECT_OPCODE
                setState(LoginDecoderState.HEADER)
            } else {
                ctx.writeResponse(LoginResultType.BAD_SESSION_ID)
            }
        }
    }

    private fun decodeHeader(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>) {
        if(buf.readableBytes() >= 3) {
            val size = buf.readUnsignedShort()
            if(buf.readableBytes() >= size) {
                val rev = buf.readInt()
                buf.skipBytes(Int.SIZE_BYTES)
                buf.skipBytes(Byte.SIZE_BYTES)

                if(rev == revision) {
                    payloadLength = size - (Int.SIZE_BYTES + Int.SIZE_BYTES + Byte.SIZE_BYTES)
                    decodePayload(ctx,buf,out)
                } else {
                    ctx.writeResponse(LoginResultType.REVISION_MISMATCH)
                }
            } else {
                buf.resetReaderIndex()
            }
        }
    }

    private fun decodePayload(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>) {
        if(buf.readableBytes() >= payloadLength) {
            buf.markReaderIndex()

            val secureBufLength = buf.readUnsignedShort()
            val secureBuffer = buf.readBytes(secureBufLength)
            val rsaValue = BigInteger(secureBuffer.array()).modPow(rsaExponent, rsaModulus)
            val secureBuf = Unpooled.wrappedBuffer(rsaValue.toByteArray())

            val decryptState = secureBuf.readUnsignedByte().toInt() == 1
            if(!decryptState) {
                buf.resetReaderIndex()
                buf.skipBytes(payloadLength)
                logger.info("Login request rejected. Client RSA encryption keys do not match the server.")
                ctx.writeResponse(LoginResultType.BAD_SESSION_ID)
                return
            }

            val xteaKeys = IntArray(4) { secureBuf.readInt() }
            val clientSeed = secureBuf.readLong()

            val authCode: Int
            val password: String?
            val previousXteaKeys = IntArray(4)

            if(reconnecting) {
                for(i in 0 .. previousXteaKeys.size) {
                    previousXteaKeys[i] = secureBuf.readInt()
                }

                authCode = -1
                password = null
            } else {
                val authType = secureBuf.readByte().toInt()

                if(authType == 1) {
                    authCode = secureBuf.readInt()
                } else if(authType == 0 || authType == 2) {
                    authCode = secureBuf.readUnsignedMedium()
                    secureBuf.skipBytes(Byte.SIZE_BYTES)
                } else {
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
                logger.info("Login request rejected for user {}. Client seed does not match the servers.", username)
                ctx.writeResponse(LoginResultType.BAD_SESSION_ID)
                return
            }

            val clientSettings = xteaBuf.readByte().toInt()
            val clientResizable = (clientSettings shr 1) == 1
            val clientWidth = xteaBuf.readUnsignedShort()
            val clientHeight = xteaBuf.readUnsignedShort()

            xteaBuf.skipBytes(24)
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

            val cachecrcs = IntArray(crcs.size) { xteaBuf.readInt() }

            for(i in 0 until cachecrcs.size) {
                if(i == 16) {
                    continue
                }

                if(cachecrcs[i] != crcs[i]) {
                    buf.resetReaderIndex()
                    buf.skipBytes(payloadLength)
                    logger.info("Login request rejected for user: {}. Client cache does not match the servers.", username)
                    ctx.writeResponse(LoginResultType.REVISION_MISMATCH)
                    return
                }
            }

            val request = LoginRequest(
                channel = ctx.channel(),
                username = username,
                password = password ?: "",
                revision = revision,
                xteaKeys = xteaKeys,
                resizableClient = clientResizable,
                auth = authCode,
                uuid = "".toUpperCase(),
                clientWidth = clientWidth,
                clientHeight = clientHeight,
                reconnecting = reconnecting
            )

            out.add(request)
        }
    }

    private fun ChannelHandlerContext.writeResponse(result: LoginResultType) {
        val buf = channel().alloc().buffer(1)
        buf.writeByte(result.id)
        writeAndFlush(buf).addListener(ChannelFutureListener.CLOSE)
    }

    private fun ByteBuf.decipher(xteaKeys: IntArray) : ByteBuf {
        val data = ByteArray(readableBytes())
        readBytes(data)
        return Unpooled.wrappedBuffer(Xtea.decipher(xteaKeys, data, 0, data.size))
    }

    companion object {
        val logger = KotlinLogging.logger {}

        private const val LOGIN_OPCODE = 16
        private const val RECONNECT_OPCODE = 18
    }
}