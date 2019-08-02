package io.rsbox.engine.system.game

import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.handler.timeout.ReadTimeoutException
import io.netty.util.AttributeKey
import io.rsbox.engine.system.ServerSystem
import io.rsbox.engine.system.auth.LoginSystem
import io.rsbox.engine.system.filestore.FilestoreSystem
import io.rsbox.engine.net.codec.handshake.HandshakeMessage
import io.rsbox.engine.net.codec.handshake.HandshakeType
import mu.KotlinLogging
import net.runelite.cache.fs.Store
import java.lang.Exception

/**
 * @author Kyle Escobar
 */

@ChannelHandler.Sharable
class GameHandler(val filestore: Store) : ChannelInboundHandlerAdapter() {

    override fun channelInactive(ctx: ChannelHandlerContext) {
        val session = ctx.channel().attr(SYSTEM_KEY).andRemove
        session?.terminate()
        ctx.channel().close()
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        try {
            val attribute = ctx.channel().attr(SYSTEM_KEY)
            val system = attribute.get()
            if(system != null) {
                system.recieveMessage(ctx, msg)
            } else if(msg is HandshakeMessage) {
                when(msg.id) {
                    HandshakeType.JS5.id -> attribute.set(FilestoreSystem(ctx.channel(), filestore))
                    HandshakeType.LOGIN.id -> attribute.set(LoginSystem(ctx.channel()))
                }
            }

        } catch (e : Exception) {
            logger.error("Error reading message $msg from channel ${ctx.channel()}.", e)
        }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        if(cause.stackTrace.isEmpty() || cause.stackTrace[0].methodName != "read0") {
            if(cause is ReadTimeoutException) {
                logger.info("Channel disconnected due to read timeout {}", ctx.channel())
            } else {
                logger.error("Channel threw an exception: ${ctx.channel()}", cause)
            }
        }
        ctx.channel().close()
    }

    companion object {
        val logger = KotlinLogging.logger {}

        val SYSTEM_KEY: AttributeKey<ServerSystem> = AttributeKey.valueOf("system")
    }
}