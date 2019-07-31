package io.rsbox.engine.system.auth

import io.rsbox.api.net.login.LoginRequest
import io.rsbox.engine.model.storage.nbt.nbt
import io.rsbox.engine.model.storage.nbt.save
import io.rsbox.engine.model.entity.Client
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

            val filename = request.username.replace(" ","_")
            val save = client.toNBT()
            save.save("rsbox/data/saves/${filename}.dat")

            return true
        } catch ( e: Exception) {
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

            client.username = save.string.get("username")!!
            client.displayName = save.string.get("display_name")!!
            client.password = save.string.get("password")!!
            client.uuid = save.string.get("uuid")!!
            client.currentXteas = save.ints.get("current_xteas")!!.toIntArray()
            client.privilege = save.int.get("privilege")!!

            return PlayerLoadResult.ACCEPTED
        } catch(e : Exception) {
            return PlayerLoadResult.INVALID_CREDENTIALS
        }
    }
}