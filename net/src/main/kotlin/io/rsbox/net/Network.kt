package io.rsbox.net

import net.runelite.cache.fs.Store
import java.math.BigInteger

/**
 * @author Kyle Escobar
 */

object Network {
    var revision: Int = -1
    lateinit var cacheStore: Store
    lateinit var rsaExponent: BigInteger
    lateinit var rsaModulus: BigInteger
}