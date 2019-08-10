package io.rsbox.server.serializer.player

import com.uchuhimo.konf.ConfigSpec

/**
 * @author Kyle Escobar
 */

object PlayerSpec : ConfigSpec("player") {
    /**
     * Account Data
     */
    val username by required<String>("username")
    val password by required<String>("password")
    val displayName by required<String>("displayName")
    val privilege by required<Int>("privilege")
    val uuid by required<String>("uuid")

    /**
     * World Data
     */
    val x by required<Int>("location.x")
    val z by required<Int>("location.z")
    val height by required<Int>("loccation.height")
}