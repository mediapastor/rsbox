package io.rsbox.api.serialization.nbt

/**
 * @author Kyle Escobar
 */

import java.io.*

fun NBT.save(location: File) {
    val out = DataOutputStream(FileOutputStream(location))
    NBTWriter.writeTagCompound(
        out,
        TagCompound((this as NBTBase).tags)
    )
    out.close()
}

fun NBT.save(location: String) = save(File(location))

fun nbt(location: File, provideDefault: Boolean = false): NBT {
    if (!location.exists() && provideDefault) return nbt()
    val di = DataInputStream(FileInputStream(location))
    val tc = NBTReader.readTagCompound(di)
    di.close()
    return NBTRoot(tc.value)
}

fun nbt(location: String, provideDefault: Boolean = false) =
    nbt(File(location), provideDefault)

object NBTReader {
    private fun readData(di: DataInput, type: Int) = when (type) {
        0    -> TagEnd()
        1    -> TagByte(di.readByte())
        2    -> TagShort(di.readShort())
        3    -> TagInt(di.readInt())
        4    -> TagLong(di.readLong())
        5    -> TagFloat(di.readFloat())
        6    -> TagDouble(di.readDouble())
        7    -> readTagArray(di, DataInput::readByte, ::TagByteArray)
        8    -> TagString(readString(di))
        9    -> readTagList(di)
        10   -> readTagCompound(di)
        11   -> readTagArray(di, DataInput::readInt, ::TagIntArray)
        12   -> readTagArray(di, DataInput::readLong, ::TagLongArray)
        else -> error("Unexpected tag type: $type")
    }

    private fun readString(di: DataInput): String {
        val len = di.readUnsignedShort()
        val buf = ByteArray(len)
        di.readFully(buf)
        return String(buf)
    }

    internal fun readTagCompound(di: DataInput): TagCompound {
        var tagMap: Map<String, TagBase> = emptyMap()
        while (true) {
            val id = di.readUnsignedByte()
            if (id == 0) break

            val name = readString(di)
            val data = readData(di, id)
            tagMap += name to data
        }
        return TagCompound(tagMap)
    }

    private fun <T, R : TagBase> readTagArray(di: DataInput, readOp: (DataInput) -> T, packOp: (List<T>) -> R) =
        packOp((0 until maxOf(0, di.readInt())).map { readOp(di) }.toList())

    private fun readTagList(di: DataInput): TagList {
        val type = di.readUnsignedByte()
        val len = maxOf(0, di.readInt())
        return if (len > 0) {
            require(type in 1..12, { "Invalid list type: $type" })
            TagList((0 until len).map {
                readData(
                    di,
                    type
                )
            })
        } else {
            TagList(emptyList())
        }
    }
}

object NBTWriter {
    private fun writeData(out: DataOutput, tag: TagBase) = when (tag) {
        is TagEnd -> Unit
        is TagByte -> out.writeByte(tag.value)
        is TagShort -> out.writeShort(tag.value)
        is TagInt -> out.writeInt(tag.value)
        is TagLong -> out.writeLong(tag.value)
        is TagFloat -> out.writeFloat(tag.value)
        is TagDouble -> out.writeDouble(tag.value)
        is TagByteArray -> writeTagArray(
            out,
            tag.value,
            { o, d -> o.writeByte(d) })
        is TagString -> writeString(
            out,
            tag.value
        )
        is TagList -> writeTagList(out, tag)
        is TagCompound -> writeTagCompound(
            out,
            tag
        )
        is TagIntArray -> writeTagArray(
            out,
            tag.value,
            DataOutput::writeInt
        )
        is TagLongArray -> writeTagArray(
            out,
            tag.value,
            DataOutput::writeLong
        )
    }

    private fun writeString(out: DataOutput, s: String) {
        val bytes = s.toByteArray()
        val len = bytes.size
        if (len > 65535) error("too big :(")
        out.writeShort(len)
        out.write(bytes)
    }

    internal fun writeTagCompound(out: DataOutput, tag: TagCompound) {
        for ((name, data) in tag.value) {
            out.writeByte(data.type)
            writeString(out, name)
            writeData(out, data)
        }
        out.writeByte(0)
    }

    private fun <T> writeTagArray(out: DataOutput, data: List<T>, writeOp: (DataOutput, T) -> Unit) {
        out.writeInt(data.size)
        data.forEach { writeOp(out, it) }
    }

    private fun writeTagList(out: DataOutput, tag: TagList) {
        out.writeByte(tag.dataType)
        out.writeInt(tag.value.size)
        tag.value.forEach { writeData(out, it) }
    }

    private fun DataOutput.writeByte(b: Byte) = writeByte(b.toInt())
    private fun DataOutput.writeShort(s: Short) = writeShort(s.toInt())
}