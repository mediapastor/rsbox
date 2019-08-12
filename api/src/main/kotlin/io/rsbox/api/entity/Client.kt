package io.rsbox.api.entity

/**
 * @author Kyle Escobar
 */

interface Client : Player {
    var clientResizable: Boolean

    var clientWidth: Int

    var clientHeight: Int

    fun register()
}