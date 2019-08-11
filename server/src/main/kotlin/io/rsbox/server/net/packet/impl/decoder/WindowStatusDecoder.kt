package io.rsbox.server.net.packet.impl.decoder

import io.rsbox.server.net.packet.MessageDecoder
import io.rsbox.server.net.packet.impl.message.WindowStatusMessage

/**
 * @author Kyle Escobar
 */

class WindowStatusDecoder : MessageDecoder<WindowStatusMessage>() {
    override fun decode(opcode: Int, opcodeIndex: Int, values: HashMap<String, Number>, stringValues: HashMap<String, String>): WindowStatusMessage {
        return WindowStatusMessage(values["mode"]!!.toInt(), values["width"]!!.toInt(), values["height"]!!.toInt())
    }
}