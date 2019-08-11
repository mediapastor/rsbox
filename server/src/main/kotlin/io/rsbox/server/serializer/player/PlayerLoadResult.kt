package io.rsbox.server.serializer.player

/**
 * @author Kyle Escobar
 */

enum class PlayerLoadResult {
    ACCEPTABLE,
    NEW_ACCOUNT,
    INVALID,
    BANNED
}