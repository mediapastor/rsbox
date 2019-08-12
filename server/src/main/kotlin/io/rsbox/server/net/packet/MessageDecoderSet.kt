package io.rsbox.server.net.packet

import io.rsbox.server.service.ServiceManager
import io.rsbox.server.service.impl.GameService

/**
 * @author Kyle Escobar
 */

class MessageDecoderSet {

    private val structures = ServiceManager.getService(GameService::class.java).messages

    private val decoders = hashMapOf<Int, MessageDecoder<*>>()

    private val handlers = hashMapOf<Int, MessageHandler<*>>()

    fun <T : Message> register(message: Class<T>, decoder: Class<MessageDecoder<Message>>, handler: Class<MessageHandler<Message>>) {
        val structure = structures.get(message) ?: throw ClassNotFoundException("Could not find the message structure class for decoder ${decoder.javaClass.simpleName}.")

        decoders[structure.opcode] = decoder.newInstance()
        handlers[structure.opcode] = handler.newInstance()
    }

    fun get(opcode: Int): MessageDecoder<*>? {
        return decoders[opcode]
    }

    @Suppress("UNCHECKED_CAST")
    fun getHandler(opcode: Int) : MessageHandler<Message>? {
        return handlers[opcode] as MessageHandler<Message>
    }

    fun count(): Int {
        return decoders.size
    }

    fun countHandlers(): Int {
        return handlers.size
    }
}