package io.rsbox.engine.system.auth

import io.rsbox.api.net.login.LoginRequest
import io.rsbox.api.serialization.nbt.fromNBT
import io.rsbox.api.serialization.nbt.nbt
import io.rsbox.api.serialization.nbt.save
import io.rsbox.engine.model.entity.RSClient
import org.mindrot.jbcrypt.BCrypt
import java.io.File
import java.util.*

/**
 * @author Kyle Escobar
 */

object PlayerSaveProvider {

    fun createPlayer(client: RSClient, request: LoginRequest): Boolean {
        try {
            client.username = request.username
            client.displayName = ""
            client.password = BCrypt.hashpw(request.password, BCrypt.gensalt(16))
            client.uuid = UUID.randomUUID().toString()
            client.currentXteas = request.xteaKeys.toList()
            client.privilege = 0

            val filename = request.username.replace(" ","_")
            val save = client.toNBT()
            save.save("rsbox/data/saves/${filename}.dat")

            return true
        } catch ( e: Exception) {
            return false
        }
    }

    fun loadPlayer(client: RSClient, request: LoginRequest): PlayerLoadResult {
        try {
            val filename = request.username.replace(" ","_")
            val file = File("rsbox/data/saves/${filename}.dat")

            if(!file.exists()) return PlayerLoadResult.NO_ACCOUNT


            val save = nbt("rsbox/data/saves/${filename}.dat")

            if(!BCrypt.checkpw(request.password, save.string.get("password"))) {
                return PlayerLoadResult.INVALID_CREDENTIALS
            }

            client.fromNBT(save)

            return PlayerLoadResult.ACCEPTED
        } catch(e : Exception) {
            return PlayerLoadResult.INVALID_CREDENTIALS
        }
    }
}