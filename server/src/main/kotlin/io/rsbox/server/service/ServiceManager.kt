package io.rsbox.server.service

import com.google.common.base.Stopwatch
import mu.KLogging
import org.reflections.Reflections
import java.util.concurrent.TimeUnit

/**
 * @author Kyle Escobar
 */

object ServiceManager : KLogging() {
    private val services = mutableListOf<Service>()

    fun init() {
        val stopwatch = Stopwatch.createStarted()

        val reflections = Reflections("io.rsbox.server")
        val classes = reflections.getSubTypesOf(Service::class.java)

        classes.forEach { clazz ->
            val inst = clazz.newInstance()
            services.add(inst)
            inst.loaded = true
            inst.onStart()

            logger.info { "Started service ${clazz.simpleName}." }
        }

        logger.info { "Successfully started ${services.size} services in ${stopwatch.elapsed(TimeUnit.MILLISECONDS)}ms." }
    }

    fun getService(clazz: Class<out Service>): Service {
        return services.associate { it.javaClass to it }[clazz] ?: throw ClassNotFoundException("Unable to find loaded class ${clazz.simpleName}.")
    }
}