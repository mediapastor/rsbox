package io.rsbox.engine.net.codec.filestore

/**
 * @author Kyle Escobar
 */

data class FilestoreRequest(val index: Int, val archive: Int, val priority: Boolean)