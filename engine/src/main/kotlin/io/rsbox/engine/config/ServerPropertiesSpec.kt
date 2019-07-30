package io.rsbox.engine.config

import com.uchuhimo.konf.ConfigSpec

/**
 * @author Kyle Escobar
 */

class ServerPropertiesSpec {
    companion object : ConfigSpec("server") {
        val name by optional("RSBox")
        val host by optional("0.0.0.0")
        val port by optional(43594)
        val revision by optional(181)
        val cycleTime by optional(600)
        val playerLimit by optional(2000)
        val rsa_privatekey by optional("rsbox/data/rsa/private.pem", "rsa.privatekey")
        val rsa_publickey by optional("rsbox/data/rsa/public.pem", "rsa.publickey")
        val rsa_exponent by optional("rsbox/data/rsa/rsa.exponent", "rsa.exponent")
        val rsa_modulus by optional("rsbox/data/rsa/rsa.modulus", "rsa.modulus")
        val auto_create_account by optional(true, "auto_create_account", "Whether or not to create account if the username does not exist.")
    }
}