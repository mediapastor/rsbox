package io.rsbox.server.service.impl

import io.rsbox.server.net.packet.MessageEncoderSet
import io.rsbox.server.net.packet.MessageStructureSet
import io.rsbox.server.net.packet.impl.MessageDecoderSet
import io.rsbox.server.service.Service
import mu.KLogging

/**
 * @author Kyle Escobar
 */

class GameService : Service() {

    lateinit var messages: MessageStructureSet
    lateinit var encoders: MessageEncoderSet
    lateinit var decoders: MessageDecoderSet

    override fun onStart() {
        messages = MessageStructureSet()
        encoders = MessageEncoderSet()
        decoders = MessageDecoderSet()

        messages.load(encoders, decoders)
    }

    override fun onStop() {

    }

    companion object : KLogging()
}