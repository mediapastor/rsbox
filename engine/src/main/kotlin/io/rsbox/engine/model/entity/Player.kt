package io.rsbox.engine.model.entity

/**
 * @author Kyle Escobar
 */

open class Player {
    lateinit var username: String

    lateinit var displayName: String

    lateinit var password: String

    lateinit var uuid: String

    lateinit var currentXteaKeys: IntArray

    var privilege: Int = 0
}