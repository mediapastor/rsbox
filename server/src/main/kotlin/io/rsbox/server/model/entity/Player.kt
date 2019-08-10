package io.rsbox.server.model.entity

/**
 * @author Kyle Escobar
 */

open class Player : LivingEntity() {
    var username = ""

    var displayName = ""

    var password = ""

    var uuid = ""

    var privilege = 0

    lateinit var client: Client
}