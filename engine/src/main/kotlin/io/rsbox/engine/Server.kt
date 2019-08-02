package io.rsbox.engine

import com.google.common.base.Stopwatch
import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.properties.toProperties
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.rsbox.engine.config.ServerPropertiesSpec
import io.rsbox.engine.model.world.RSWorld
import io.rsbox.engine.service.ServiceProvider
import io.rsbox.engine.service.impl.GameService
import io.rsbox.engine.service.impl.LoginService
import io.rsbox.engine.net.codec.login.LoginRequest
import io.rsbox.engine.system.modules.rsa.RsaKey
import io.rsbox.engine.net.packet.ClientChannelInitializer
import mu.KotlinLogging
import net.runelite.cache.fs.Store
import java.io.File
import java.nio.file.Paths
import java.util.concurrent.TimeUnit

/**
 * @author Kyle Escobar
 */

class Server(val filestorePath: String, args: Array<String>) {

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

    lateinit var config: Config

    private val acceptGroup = NioEventLoopGroup(2)
    private val ioGroup = NioEventLoopGroup(1)
    val bootstrap = ServerBootstrap()

    lateinit var gameContext: GameContext

    val serviceProvider = ServiceProvider(this)

    lateinit var world: RSWorld

    fun init() {
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

        world.cacheStore = filestore
        world.serviceProvider = serviceProvider

        logger.info { "Loaded cache from path $filestorePath in ${stopwatch.elapsed(TimeUnit.MILLISECONDS)}ms."}

        serviceProvider.loadEngineServices()

        serviceProvider.getService(GameService::class.java)!!.let { gameService ->
            gameService.packetStructures.load()
            gameService.packetEncoders.init()
            gameService.packetDecoders.init(gameService.packetStructures)
        }


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

    fun queueLoginRequest(request: LoginRequest) {
        serviceProvider.getService(LoginService::class.java)!!.queueLoginRequest(request)
    }

    companion object {
        val logger = KotlinLogging.logger {}
        val loggerInst = KotlinLogging.logger {}
    }
}