package io.rsbox.engine.serialization.nbt

/**
 * @author Kyle Escobar
 */

fun <T : TagBase?> T.asByte() = when (this) {
    is TagByte -> value
    is TagShort -> value.toByte()
    is TagInt -> value.toByte()
    is TagLong -> value.toByte()
    is TagFloat -> value.toByte()
    is TagDouble -> value.toByte()
    else         -> null
}

fun <T : TagBase?> T.asShort() = when (this) {
    is TagByte -> value.toShort()
    is TagShort -> value
    is TagInt -> value.toShort()
    is TagLong -> value.toShort()
    is TagFloat -> value.toShort()
    is TagDouble -> value.toShort()
    else         -> null
}

fun <T : TagBase?> T.asInt() = when (this) {
    is TagByte -> value.toInt()
    is TagShort -> value.toInt()
    is TagInt -> value
    is TagLong -> value.toInt()
    is TagFloat -> value.toInt()
    is TagDouble -> value.toInt()
    else         -> null
}

fun <T : TagBase?> T.asLong() = when (this) {
    is TagByte -> value.toLong()
    is TagShort -> value.toLong()
    is TagInt -> value.toLong()
    is TagLong -> value
    is TagFloat -> value.toLong()
    is TagDouble -> value.toLong()
    else         -> null
}

fun <T : TagBase?> T.asFloat() = when (this) {
    is TagByte -> value.toFloat()
    is TagShort -> value.toFloat()
    is TagInt -> value.toFloat()
    is TagLong -> value.toFloat()
    is TagFloat -> value
    is TagDouble -> value.toFloat()
    else         -> null
}

fun <T : TagBase?> T.asDouble() = when (this) {
    is TagByte -> value.toDouble()
    is TagShort -> value.toDouble()
    is TagInt -> value.toDouble()
    is TagLong -> value.toDouble()
    is TagFloat -> value.toDouble()
    is TagDouble -> value
    else         -> null
}

fun <T : TagBase?> T.asByteArray() = when (this) {
    is TagByteArray -> value
    is TagIntArray -> value.map { it.toByte() }
    is TagLongArray -> value.map { it.toByte() }
    else            -> null
}

fun <T : TagBase?> T.asString() = when (this) {
    is TagString -> value
    else         -> null
}

fun <T : TagBase?> T.asTagList() = when (this) {
    is TagList -> value
    else       -> null
}

fun <T : TagBase?> T.asTagCompound() = when (this) {
    is TagCompound -> value
    else           -> null
}

fun <T : TagBase?> T.asIntArray() = when (this) {
    is TagByteArray -> value.map { it.toInt() }
    is TagIntArray -> value
    is TagLongArray -> value.map { it.toInt() }
    else            -> null
}

fun <T : TagBase?> T.asLongArray() = when (this) {
    is TagByteArray -> value.map { it.toLong() }
    is TagIntArray -> value.map { it.toLong() }
    is TagLongArray -> value
    else            -> null
}