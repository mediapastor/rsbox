package io.rsbox.net.codec.game

/**
 * @author Kyle Escobar
 */

enum class GameDecoderState {
    OPCODE,
    LENGTH,
    PAYLOAD
}