package io.rsbox.api

import io.rsbox.api.event.Event
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import org.reflections.Reflections
import kotlin.reflect.KClass

/**
 * The object which holds references to all events and listeners
 */
object EventManager {
    /**
     * A List of events classes for reference.
     */
    private val events = mutableListOf<KClass<out Event>>()

    /**
     * A list of even classes and logic to be executed when the event key is triggered
     */
    private val listeners = Object2ObjectOpenHashMap<KClass<out Event>, (event: Event) -> Unit>()

    internal fun init() {
        val reflections = Reflections("io.rsbox.api")
        val classes = reflections.getSubTypesOf(Event::class.java)

        classes.forEach { clazz ->
            events.add(clazz.kotlin)
        }

        Api.server.logger.info { "Successfully registered ${events.size} API events." }
    }

    /**
     * Registers the listener logic into the open hash map.
     *
     * @param eventClass The class which the logic applies to
     * @param logic Lambda logic for the listener that will be executed when the event is triggered.
     */
    internal fun registerListener(eventClass: KClass<out Event>, logic: Event.() -> Unit) {
        listeners[eventClass] = logic
    }

    /**
     * Used to trigger an event. This takes a new instance of the event.
     *
     * @param event The instance of the event being triggered.
     * @return [Boolean] If the event is cancelled or not. If the event cannot be cancelled, this value is always true.
     */
    fun <T : Event> trigger(event: T): Boolean {
        // Execute the listeners
        listeners.forEach { listener ->
            if(listener.key == event::class) {
                listener.value(event)
            }
        }

        return event.handler()
    }
}