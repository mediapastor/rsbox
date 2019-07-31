package io.rsbox.engine.model

/**
 * @author Kyle Escobar
 */

enum class Privilege(val id: Int) {
    DEFAULT(0),

    PLAYERMOD(1),

    ADMIN(2),

    DEVELOPER(3)
}