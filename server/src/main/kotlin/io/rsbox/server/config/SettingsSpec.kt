package io.rsbox.server.config

import com.uchuhimo.konf.ConfigSpec

/**
 * @author Kyle Escobar
 */

object SettingsSpec : ConfigSpec("server") {
    val name by optional("RSBox Server", "name")
    val ip by optional("0.0.0.0", "ip")
    val port by optional(43594, "port")
    val country by optional("US", "country")
    val members by optional(true, "members")
    val pvp by optional(false, "pvp")
    val description by optional("A RSBox World", "description")
    val revision by optional(181, "revision")
    val auto_create_users by optional(true, "auto_create_users")
    val player_saves_format by optional("yaml", "player_saves_format", "Option values: yaml json")
    val home_x by optional(3221, "home.x")
    val home_z by optional(3218, "home.z")
    val home_height by optional(0, "home.height")
}