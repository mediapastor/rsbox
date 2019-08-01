package io.rsbox.api.net.packet

/**
 * @author Kyle Escobar
 */

enum class DataOrder {

    /**
     * Most significant byte to least significant byte.
     */
    BIG,

    /**
     * Also known as the V2 order.
     */
    INVERSED_MIDDLE,

    /**
     * Least significant byte to most significant byte.
     */
    LITTLE,

    /**
     * Also known as the V1 order.
     */
    MIDDLE

}