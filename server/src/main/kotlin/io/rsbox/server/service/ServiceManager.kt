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
            logger.info { "Started service ${clazz.simpleName}." }

            inst.onStart()
        }

        logger.info { "Successfully started ${services.size} services in ${stopwatch.elapsed(TimeUnit.MILLISECONDS)}ms." }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T: Service> getService(clazz: Class<T>): T {
        val map = services.associate { it::class.java to it }
        return map[clazz] as T? ?: throw ClassNotFoundException("Unable to locate loaded service class ${clazz.simpleName}.")
    }
}