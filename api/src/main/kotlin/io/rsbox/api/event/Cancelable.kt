package io.rsbox.api.event

/**
 * Implemented on event to indicate wheter or not the event can be cancelled by the API.
 */
interface Cancelable {
    /**
     * Whether or not the event is cancelled. By default should be set to false in the subclass.
     */
    var cancelled: Boolean
}