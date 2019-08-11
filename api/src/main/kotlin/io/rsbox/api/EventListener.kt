package io.rsbox.api

import io.rsbox.api.event.Event
import kotlin.reflect.KClass

/**
 * Represents a set of logic to be applied to an [Event]
 * The logic gets executed when the event is triggered.
 */
class EventListener {
    /**
     * The event kotlin class that the logic should be executed when triggered.
     */
    internal lateinit var eventClass: KClass<out Event>
    /**
     * The lambda logic to be executed.
     */
    internal lateinit var logic: Event.() -> Unit

    /**
     * Registers the listener's logic with the [EventManager]
     */
    internal fun register() {
        EventManager.registerListener(eventClass, logic)
    }

    companion object {
        /**
         * The static event listener caller.
         *
         * @param event The Event's kotlin class
         * @param logic The lambda logic to be executed.
         */
        @Suppress("UNCHECKED_CAST")
        fun <T : Event> on_event(event: KClass<T>, logic: T.() -> Unit) {
            val inst = EventListener()
            inst.eventClass = event
            inst.logic = logic as Event.() -> Unit
            inst.register()
        }
    }
}