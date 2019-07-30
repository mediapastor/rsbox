package io.rsbox.api.storage.nbt

/**
 * @author Kyle Escobar
 */

fun nbt(): NBT = NBTRoot()

interface NBT {
    val bool: NBTView<String, Boolean?>
    val byte: NBTView<String, Byte?>
    val short: NBTView<String, Short?>
    val int: NBTView<String, Int?>
    val long: NBTView<String, Long?>
    val float: NBTView<String, Float?>
    val double: NBTView<String, Double?>
    val string: NBTView<String, String?>
    val nbt: NBTView<String, NBT>

    val bools: NBTView<String, List<Boolean>?>
    val bytes: NBTView<String, List<Byte>?>
    val shorts: NBTView<String, List<Short>?>
    val ints: NBTView<String, List<Int>?>
    val longs: NBTView<String, List<Long>?>
    val floats: NBTView<String, List<Float>?>
    val doubles: NBTView<String, List<Double>?>
    val strings: NBTView<String, List<String>?>
    val nbts: NBTView<String, List<NBT>?>

    val keys: Collection<String>
    fun remove(key: String)

    operator fun contains(name: String): Boolean

    fun copy(): NBT
}

internal abstract class NBTBase : NBT {
    override val bool = sv({ it.asByte()?.let { it != 0.toByte() } }, { TagByte(if (it) -1 else 0) })
    override val byte = sv({ it.asByte() }, ::TagByte)
    override val short = sv({ it.asShort() }, ::TagShort)
    override val int = sv({ it.asInt() }, ::TagInt)
    override val long = sv({ it.asLong() }, ::TagLong)
    override val float = sv({ it.asFloat() }, ::TagFloat)
    override val double = sv({ it.asDouble() }, ::TagDouble)
    override val string = sv({ it.asString() }, ::TagString)
    override val nbt = view<String, NBT>({ key -> NBTSubtree(key, this) }, { key, value -> tags += key to TagCompound((value as NBTBase).tags) })

    override val bools = list({ it.asByte()?.let { it != 0.toByte() } ?: false }, { TagByte(if (it) -1 else 0) })
    override val bytes = sv({ it.asByteArray() }, ::TagByteArray)
    override val shorts = list({ it.asShort() ?: 0 }, ::TagShort)
    override val ints = sv({ it.asIntArray() }, ::TagIntArray)
    override val longs = sv({ it.asLongArray() }, ::TagLongArray)
    override val floats = list({ it.asFloat() ?: 0f }, ::TagFloat)
    override val doubles = list({ it.asDouble() ?: 0.0 }, ::TagDouble)
    override val strings = list({ it.asString() ?: "" }, ::TagString)
    override val nbts = list({ NBTRoot(it.asTagCompound() ?: emptyMap()) as NBT }, { TagCompound((it as NBTBase).tags) })

    override val keys: Collection<String>
        get() = tags.keys

    override fun remove(key: String) {
        tags -= key
    }

    override fun contains(name: String): Boolean = name in tags

    override fun copy() = NBTRoot(tags)

    private fun <T : Any> sv(read: (TagBase?) -> T?, write: (T) -> TagBase?) =
        view<String, T?>({ key -> read(getv(key)) }, { key, value -> setv(key, value?.let(write)) })

    private fun <T : Any> list(read: (TagBase) -> T, write: (T) -> TagBase) =
        view<String, List<T>?>({ key -> getv(key).asTagList()?.map(read) }, { key, value -> setv(key, value?.map(write)?.let(::TagList)) })

    fun getv(key: String): TagBase? {
        return tags[key]
    }

    fun setv(key: String, v: TagBase?) {
        if (v == null) tags -= key
        else tags += key to v
    }

    abstract var tags: Map<String, TagBase>
}

internal class NBTRoot(override var tags: Map<String, TagBase>) : NBTBase() {
    constructor() : this(emptyMap())
}

internal data class NBTSubtree(val id: String, val parent: NBTBase) : NBTBase() {
    override var tags: Map<String, TagBase>
        get() = parent.getv(id).asTagCompound() ?: emptyMap()
        set(value) = parent.setv(id, TagCompound(value))
}