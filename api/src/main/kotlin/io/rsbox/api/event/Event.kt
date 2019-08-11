package io.rsbox.api.event

/**
 * Represents an event that can be triggered.
 */
abstract class Event {
    abstract fun handler(): Boolean
}