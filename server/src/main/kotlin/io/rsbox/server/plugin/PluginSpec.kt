package io.rsbox.server.plugin

import com.uchuhimo.konf.ConfigSpec

/**
 * @author Kyle Escobar
 */

object PluginSpec : ConfigSpec("plugin") {
    val name by required<String>("name")
    val version by optional("1.0", "version")
    val main by required<String>("main")
    val authors by optional<Array<String>>(arrayOf(), "authors")
    val author by optional("", "author")
}