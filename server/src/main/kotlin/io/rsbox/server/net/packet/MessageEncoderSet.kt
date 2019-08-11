package io.rsbox.server.net.packet

/**
 * @author Kyle Escobar
 */

class MessageEncoderSet {
    private val encoders = hashMapOf<Class<out Message>, MessageEncoder<out Message>>()

    fun <T : Message> register(message: Class<out T>, encoder: Class<MessageEncoder<T>>) {
        val inst = encoder.newInstance() ?: throw ClassNotFoundException("Unable to create an instance of encoder class for message ${message.simpleName}.")
        encoders[message] = inst
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Message> get(type: Class<out T>) : MessageEncoder<Message>? {
        return encoders[type] as? MessageEncoder<Message>
    }

    fun count(): Int {
        return encoders.size
    }
}