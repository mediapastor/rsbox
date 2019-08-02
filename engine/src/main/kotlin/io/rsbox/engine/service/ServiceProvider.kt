package io.rsbox.engine.service

import io.rsbox.engine.Server
import mu.KotlinLogging
import org.reflections.Reflections

/**
 * @author Kyle Escobar
 */

class ServiceProvider(private val server: Server) {
    private val services = mutableListOf<Service>()

    @Suppress("UNCHECKED_CAST")
    fun <T: Service> getService(service: Class<out T>): T? {
        return services.firstOrNull { it::class.java == service } as T?
    }

    fun <T: Service> loadService(service: Class<out T>) {
        val s = service.newInstance() as Service
        s.onStart(server)
        services.add(s)
        logger.info("Started service [${service.simpleName}]")
    }

    fun terminateService(service: Class<out Service>) {
        val s = services.associate { it.javaClass to it }[service] ?: return
        s.onStop(server)
        services.remove(s)
        logger.info("Stopped service [${service.simpleName}]")
    }

    internal fun loadEngineServices() {
        if(services.size > 0) {
            logger.warn { "Failed to load engine services. Engine services have already been loaded." }
            return
        }

        val r = Reflections("io.rsbox.engine")
        val classes = r.getSubTypesOf(Service::class.java)

        classes.forEach { serviceClass ->
            if(getService(serviceClass) == null) {
                loadService(serviceClass)
            } else {
                logger.warn("Unable to load service [{}] as it is running.")
            }
        }

        logger.info("Successfully loaded {} engine services.", services.size)
    }

    companion object {
        val logger = KotlinLogging.logger {}
    }
}