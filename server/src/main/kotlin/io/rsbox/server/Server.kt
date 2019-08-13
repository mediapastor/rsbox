package io.rsbox.server

import com.google.common.base.Stopwatch
import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.yaml.toYaml
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.rsbox.api.Api
import io.rsbox.api.EventManager
import io.rsbox.api.event.ServerStartEvent
import io.rsbox.game.Game
import io.rsbox.server.net.ClientChannelHandler
import io.rsbox.server.config.SettingsSpec
import io.rsbox.server.def.DefinitionSet
import io.rsbox.server.model.world.World
import io.rsbox.server.net.rsa.RSA
import io.rsbox.server.plugin.PluginLoader
import io.rsbox.server.service.ServiceManager
import mu.KLogging
import net.runelite.cache.fs.Store
import java.io.File
import java.net.InetSocketAddress
import java.util.concurrent.TimeUnit

/**
 * @author Kyle Escobar
 */

class Server : io.rsbox.api.Server {

    /**
     * Public variables
     */
    lateinit var cacheStore: Store

    val definitions: DefinitionSet = DefinitionSet()

    val revision: Int = settings[SettingsSpec.revision]

    lateinit var rsworld: World
    override lateinit var world: io.rsbox.api.world.World

    /**
     * Networking vars
     */

    private val acceptGroup = NioEventLoopGroup(2)
    private val ioGroup = NioEventLoopGroup(1)
    private val bootstrap = ServerBootstrap()

    private val dirs = arrayOf(
        "rsbox/",
        "rsbox/data",
        "rsbox/config",
        "rsbox/data/cache",
        "rsbox/data/xteas",
        "rsbox/data/def",
        "rsbox/data/rsa",
        "rsbox/data/saves",
        "rsbox/plugins"
    )

    private val mainStopwatch = Stopwatch.createStarted()

    private val pluginLoader = PluginLoader(this)

   fun init() {
       /**
        * Hook into the API
        */
       Api.server = this
       Api.init()

       logger.info { "Server starting initialization..." }

       logger.info { "Scanning directories." }
       initDirs()

       logger.info{"Scanning for configs."}
       initConfigs()

       initCache()

       definitions.loadAll(cacheStore)

       /**
        * Hook shutdown event for proper shutdowns
        */
       //interceptShutdown { this.shutdown() }

       RSA.init()

       /**
        * Load the world
        */
       rsworld = World(this)
       rsworld.init()
       world = rsworld
       logger.info { "Loaded the game world." }

       /**
        * Start the services via [ServiceManager]
        */
       ServiceManager.init()

       Game.init()

       logger.info { "Loading plugins..." }
       loadPlugins()

       start()
   }


    private fun start() {
        logger.info { "Preparing server." }
        startNetworking()

        if(EventManager.trigger(ServerStartEvent(this))) {
            logger.warn { "Server shutdown signal received." }
            System.exit(0)
        }
    }

    fun shutdown() {
        println("Shutdown")
    }

    private fun startNetworking() {
        logger.info { "Starting server networking." }
        bootstrap.group(acceptGroup, ioGroup)
        bootstrap.channel(NioServerSocketChannel::class.java)
        bootstrap.childHandler(ClientChannelHandler())
        bootstrap.option(ChannelOption.TCP_NODELAY, true)
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true)
        bootstrap.bind(InetSocketAddress(settings[SettingsSpec.port])).sync().awaitUninterruptibly()

        logger.info("Server completed start in {}ms.", mainStopwatch.elapsed(TimeUnit.MILLISECONDS))
        logger.info("Listening for incoming connections on {}:{}...", InetSocketAddress(settings[SettingsSpec.port]).address, settings[SettingsSpec.port])
    }

    private fun initDirs() {
        dirs.forEach { dir ->
            val file = File(dir)
            if(!file.exists()) {
                file.mkdirs()
                logger.info("Created default directory {} as it did not exist.", dir)
            }
        }
    }

    private fun initConfigs() {
        if(!File(ServerConstants.SETTINGS_CONFIG_PATH).exists()) {
            settings.toYaml.toFile(ServerConstants.SETTINGS_CONFIG_PATH)
            logger.info("Created default settings.yml as it did not exist.")
        } else {
            settings = Config { addSpec(SettingsSpec) }.from.yaml.file(ServerConstants.SETTINGS_CONFIG_PATH)
            logger.info { "Loaded server settings from ${ServerConstants.SETTINGS_CONFIG_PATH}." }

            /**
             * Save the current config in case new defaults are not saved.
             */
            settings.toYaml.toFile(ServerConstants.SETTINGS_CONFIG_PATH)
        }
    }

    private fun initCache() {
        val cacheDir = File(ServerConstants.CACHE_PATH)
        val xteasFile = File(ServerConstants.XTEAS_PATH)

        val stopwatch = Stopwatch.createStarted()

        /**
         * Load cache using runelite tools
         */
        cacheStore = Store(cacheDir)
        cacheStore.load()

        if(cacheStore.indexes.size == 0) {
            logger.error { "It appears you are missing the OSRS cache files in ./rsbox/data/cache/." }
            logger.error { "Please put the required OSRS revision cache files in this folder and restart the server." }
            System.exit(1)
        }

        if(!xteasFile.exists()) {
            logger.error { "It appears you are missing the cache decryption keys file. (./rsbox/data/xteas/xteas.json)" }
            logger.error { "Please put the xteas.json file in this folder and restart the server." }
            System.exit(1)
        }

        stopwatch.stop()

        logger.info("Loaded the server cache files in {}ms.", stopwatch.elapsed(TimeUnit.MILLISECONDS))
    }

    private fun loadPlugins() {
        val path = File(ServerConstants.PLUGINS_PATH)
        val files = path.listFiles().filter { it.isFile }
        files.forEach { jar ->
            pluginLoader.loadPlugin(jar)
        }
    }

    private interface ShutdownHook {
        fun abort()
    }

    private fun interceptShutdown(logic: () -> Unit) : ShutdownHook {
        val hook = Thread { logic() }
        val runtime = Runtime.getRuntime()
        runtime.addShutdownHook(hook)
        return object : ShutdownHook {
            override fun abort() {
                if(Thread.currentThread() != hook) {
                    runtime.removeShutdownHook(hook)
                }
            }
        }
    }

    companion object : KLogging() {
        var settings = Config { addSpec(SettingsSpec) }
    }
}