package io.rsbox.engine.system.auth

import io.rsbox.engine.Server
import io.rsbox.engine.serialization.nbt.fromNBT
import io.rsbox.engine.serialization.nbt.nbt
import io.rsbox.engine.serialization.nbt.save
import io.rsbox.engine.model.entity.Client
import io.rsbox.engine.model.world.Tile
import io.rsbox.engine.net.codec.login.LoginRequest
import org.mindrot.jbcrypt.BCrypt
import java.io.File
import java.util.*

/**
 * @author Kyle Escobar
 */

object PlayerSaveProvider {

    fun createPlayer(client: Client, request: LoginRequest): Boolean {
        try {
            client.username = request.username
            client.displayName = ""
            client.password = BCrypt.hashpw(request.password, BCrypt.gensalt(16))
            client.uuid = UUID.randomUUID().toString()
            client.currentXteas = request.xteaKeys
            client.privilege = 0

            client.tile = Tile(3221,3218,0)

            val filename = request.username.replace(" ","_")
            val save = client.toNBT()
            save.save("rsbox/data/saves/${filename}.dat")

            client.fromNBT(nbt("rsbox/data/saves/${filename}.dat"))

            return true
        } catch ( e: Exception) {
            Server.logger.error("An error occured created player.", e)
            return false
        }
    }

    fun loadPlayer(client: Client, request: LoginRequest): PlayerLoadResult {
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
            Server.logger.error { "Unable to load player save file [username=${request.username}]. File might be corrupted." }
            throw Exception(e)
            return PlayerLoadResult.INVALID_CREDENTIALS
        }
    }
}