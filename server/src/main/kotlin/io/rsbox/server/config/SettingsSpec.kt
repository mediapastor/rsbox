package io.rsbox.server.config

import com.uchuhimo.konf.ConfigSpec

/**
 * @author Kyle Escobar
 */

object SettingsSpec : ConfigSpec("server") {
    val name by optional("RSBox Server")
    val port by optional(43594)
    val revision by optional(181)
}