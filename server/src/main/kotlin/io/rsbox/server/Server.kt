package io.rsbox.server

import com.google.common.base.Stopwatch
import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.yaml.toYaml
import io.rsbox.server.config.ServerSettingsSpec
import io.rsbox.server.net.NetworkReactor
import mu.KLogging
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * @author Kyle Escobar
 */

class Server {

    val config: Config = Config { addSpec(ServerSettingsSpec) }

    private lateinit var reactor: NetworkReactor

    private val stopwatch: Stopwatch = Stopwatch.createStarted()

    private val dirs = arrayOf(
        "rsbox/",
        "rsbox/config",
        "rsbox/data/",
        "rsbox/data/cache",
        "rsbox/data/xteas",
        "rsbox/data/def",
        "rsbox/plugins"
    )

    fun init() {
        logger.info { "Starting server..." }

        checkDirs()
        checkConfig()

        start()
    }

    private fun start() {
        logger.info { "Starting Network Reactor..." }
        stopwatch.reset().start()

        NetworkReactor.revision = config[ServerSettingsSpec.revision]
        reactor = NetworkReactor.configure(config[ServerSettingsSpec.port])
        reactor.start()
        logger.info("{} started successfully in {}ms.", config[ServerSettingsSpec.name], stopwatch.elapsed(TimeUnit.MILLISECONDS))
        logger.info("Listening for incoming connections on port {}...", config[ServerSettingsSpec.port])
    }

    private fun checkDirs() {
        dirs.forEach { dir ->
            val f = File(dir)
            if(!f.exists()) {
                f.mkdirs()
                logger.info("Creating default directory {} as it does not exist.", dir)
            }
        }
    }

    private fun checkConfig() {
        val configFile = File("rsbox/config/server.settings.yml")
        if(!configFile.exists()) {
            config.toYaml.toFile(configFile)
            logger.info("Creating default server.settings.yml as it does not exist.")
        } else {
            config.from.yaml.file(configFile)
            logger.info("Loading config file server.settings.yml")
        }
    }

    companion object : KLogging()
}