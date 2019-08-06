package io.rsbox.server.net.protocol.impl

import io.rsbox.server.net.NetworkSession
import io.rsbox.server.net.event.impl.HandshakeReadEvent
import io.rsbox.server.net.protocol.NetworkProtocol
import io.rsbox.server.net.event.NetworkReadEvent
import java.nio.ByteBuffer

/**
 * @author Kyle Escobar
 */

class HandshakeProtocol : NetworkProtocol {
    override fun invokeReader(session: NetworkSession, buffer: ByteBuffer): NetworkReadEvent {
        return HandshakeReadEvent(session, buffer)
    }
}