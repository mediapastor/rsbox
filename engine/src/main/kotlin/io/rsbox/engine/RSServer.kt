package io.rsbox.engine

import com.google.common.base.Stopwatch
import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.properties.toProperties
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.rsbox.api.RSBox
import io.rsbox.api.Server
import io.rsbox.api.World
import io.rsbox.api.net.login.LoginRequest
import io.rsbox.engine.config.ServerPropertiesSpec
import io.rsbox.engine.model.RSWorld
import io.rsbox.engine.service.ServiceProvider
import io.rsbox.engine.service.impl.LoginService
import io.rsbox.net.modules.rsa.RsaKey
import io.rsbox.net.protocol.ClientChannelInitializer
import mu.KotlinLogging
import net.runelite.cache.fs.Store
import java.io.File
import java.nio.file.Paths
import java.util.concurrent.TimeUnit

/**
 * @author Kyle Escobar
 */

class RSServer(val filestorePath: String, args: Array<String>) : Server {

    private val dirs = arrayOf(
        "rsbox/configs",
        "rsbox/data/cache",
        "rsbox/data/xteas",
        "rsbox/data/saves",
        "rsbox/data/defs",
        "rsbox/data/rsa",
        "rsbox/logs",
        "rsbox/plugins"
    )

    override lateinit var config: Config

    private val acceptGroup = NioEventLoopGroup(2)
    private val ioGroup = NioEventLoopGroup(1)
    val bootstrap = ServerBootstrap()

    lateinit var gameContext: GameContext

    val serviceProvider = ServiceProvider(this)

    override lateinit var world: World

    fun init() {

        RSBox.server = this

        logger.info { "Initializing server..." }
        this.setupDirs()

        logger.info { "Loading configurations" }
        this.loadConfigs()

        logger.info { "Starting server" }
        this.startServer()
    }

    private fun startServer() {
        Thread.setDefaultUncaughtExceptionHandler { t, e -> logger.error("Uncaught server exception in thread $t!", e) }

        val stopwatch = Stopwatch.createStarted()

        gameContext = GameContext(
            config.get("server.name"),
            config.get("server.revision"),
            config.get("server.cycleTime"),
            config.get("server.playerLimit")
        )

        world = RSWorld(gameContext)

        stopwatch.reset().start()

        logger.info { "Loading cache store." }

        val filestore = Store(Paths.get(filestorePath).toFile())
        filestore.load()
        logger.info { "Loaded cache from path $filestorePath in ${stopwatch.elapsed(TimeUnit.MILLISECONDS)}ms."}

        serviceProvider.loadEngineServices()


        val rsa = RsaKey()
        val clientChannelInitializer = ClientChannelInitializer(
            revision = config["server.revision"],
            rsaExponent = rsa.getExponent(),
            rsaModulus = rsa.getModulus(),
            filestore = filestore
        )

        bootstrap.group(acceptGroup, ioGroup)
        bootstrap.channel(NioServerSocketChannel::class.java)
        bootstrap.childHandler(clientChannelInitializer)
        bootstrap.option(ChannelOption.TCP_NODELAY, true)
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true)

        val address = config.get<String>("server.host")
        val port = config.get<Int>("server.port")

        bootstrap.bind(address, port).sync().awaitUninterruptibly()
        logger.info { "Server started. Listening for connections on ${address}:${port}..." }

        System.gc()
    }


    private fun setupDirs() {
        dirs.forEach { dir ->
            val file = File(dir)
            if(!file.exists()) {
                file.mkdirs()
                logger.info { "Creating required directory [$dir]" }
            }
        }
    }

    private fun loadConfigs() {
        if(!File("rsbox/configs/server.properties").exists()) {
            Config { addSpec(ServerPropertiesSpec) }.toProperties.toFile("rsbox/configs/server.properties")
            logger.info { "Created default [server.properties] configuration" }
        }

        config = Config {
            addSpec(ServerPropertiesSpec)
        }
            .from.properties.file("rsbox/configs/server.properties")

        logger.info { "Loaded ${config.specs.size} configurations" }
    }

    override fun queueLoginRequest(request: LoginRequest) {
        serviceProvider.getService(LoginService::class.java)!!.queueLoginRequest(request)
    }

    companion object {
        val logger = KotlinLogging.logger {}
    }
}