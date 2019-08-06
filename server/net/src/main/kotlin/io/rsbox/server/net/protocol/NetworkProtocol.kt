package io.rsbox.server.net.protocol

import io.rsbox.server.net.NetworkSession
import io.rsbox.server.net.event.NetworkReadEvent
import java.nio.ByteBuffer

/**
 * @author Kyle Escobar
 */

interface NetworkProtocol {
    fun invokeReader(session: NetworkSession, buffer: ByteBuffer): NetworkReadEvent
}