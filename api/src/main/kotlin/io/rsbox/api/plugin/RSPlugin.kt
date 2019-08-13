package io.rsbox.api.plugin

import io.rsbox.api.Api
import java.io.File

/**
 * @author Kyle Escobar
 */

abstract class RSPlugin {
    val server = Api.server

    val world = Api.server.world

    var dataFolder: File? = null
    var isEnabled = false
    var name = ""
    var main = ""
    var version = ""
    var authors = arrayOf<String>()
    var author = ""

    abstract fun onStart()

    abstract fun onStop()

    companion object {
        val instance = this
    }
}