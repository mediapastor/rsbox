package io.rsbox.engine.packets

import io.rsbox.engine.net.packet.Packet
import io.rsbox.engine.net.packet.PacketEncoder
import io.rsbox.engine.packets.encoder.PacketOutRebuildLoginEncoder
import io.rsbox.engine.packets.encoder.PacketOutRebuildNormalEncoder
import io.rsbox.engine.packets.encoder.PacketOutRebuildRegionEncoder
import io.rsbox.engine.packets.impl.PacketOutRebuildLogin
import io.rsbox.engine.packets.impl.PacketOutRebuildNormal
import io.rsbox.engine.packets.impl.PacketOutRebuildRegion
import mu.KotlinLogging

/**
 * @author Kyle Escobar
 */

class PacketEncoderSet {

    private val encoders = hashMapOf<Class<out Packet>, PacketEncoder<out Packet>>()

    fun init() {
        put(PacketOutRebuildLoginEncoder(), PacketOutRebuildLogin::class.java)
        put(PacketOutRebuildRegionEncoder(), PacketOutRebuildRegion::class.java)
        put(PacketOutRebuildNormalEncoder(), PacketOutRebuildNormal::class.java)

        logger.info("Loaded ${encoders.size} packet encoders.")
    }

    private fun <T : Packet> put(encoder: PacketEncoder<T>, packet: Class<out T>) {
        encoders[packet] = encoder
    }

    @Suppress("UNCHECKED_CAST")
    fun <T: Packet> get(type: Class<out T>): PacketEncoder<Packet>? {
        return encoders[type] as? PacketEncoder<Packet>
    }

    companion object {
        val logger = KotlinLogging.logger {}
    }
}