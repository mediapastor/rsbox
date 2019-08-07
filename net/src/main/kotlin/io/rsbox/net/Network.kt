package io.rsbox.net

import net.runelite.cache.fs.Store

/**
 * @author Kyle Escobar
 */

object Network {
    var revision: Int = -1
    lateinit var cacheStore: Store
}