package io.rsbox.server.serializer.player

import com.uchuhimo.konf.Config
import io.rsbox.server.ServerConstants
import org.mindrot.jbcrypt.BCrypt
import java.io.File

/**
 * @author Kyle Escobar
 */

object PlayerSave {
    fun check(username: String, password: String): PlayerLoadResult {
        val rusername = filterUsername(username)
        val file = File("${ServerConstants.SAVES_PATH}$rusername.yml")

        if(!file.exists()) {
            return PlayerLoadResult.NEW_ACCOUNT
        }

        val save = Config { addSpec(PlayerSpec) }.from.yaml.file(file)

        if(!BCrypt.checkpw(password, save[PlayerSpec.password])) {
            return PlayerLoadResult.INVALID
        }

        return PlayerLoadResult.ACCEPTABLE
    }

    fun filterUsername(username: String): String {
        val regex = Regex("[^A-Za-z0-9]")
        return regex.replace(username, "")
    }
}