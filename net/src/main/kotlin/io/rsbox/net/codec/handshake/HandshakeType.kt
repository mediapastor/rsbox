package io.rsbox.net.codec.handshake

/**
 * @author Kyle Escobar
 */

enum class HandshakeType(val id: Int) {
    LOGIN(14),
    JS5(15);

    companion object {
        val values = enumValues<HandshakeType>()
    }
}