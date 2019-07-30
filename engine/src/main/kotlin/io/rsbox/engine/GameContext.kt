package io.rsbox.engine

/**
 * @author Kyle Escobar
 */

data class GameContext(
    val name: String,
    val revision: Int,
    val cycleTime: Int,
    val playerLimit: Int
)