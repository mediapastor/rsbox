package io.rsbox.api.serialization.nbt

import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

/**
 * @author Kyle Escobar
 */

@Suppress("UNCHECKED_CAST")
open class NBTSerializable {
    fun toNBT(): NBT {
        val clazz = this::class
        val data = nbt()
        clazz.memberProperties.forEach {
            val tag = it.findAnnotation<NBTTag>()
            if(tag != null) {
                val name = tag.name
                val value = it.getter.call(this)
                val dataType = it.returnType.toString().replace("kotlin.","")
                when(dataType) {
                    "String" -> data.string.set(name, value as String)
                    "Int" -> data.int.set(name, value as Int)
                    "IntArray" -> data.ints.set(name, value as List<Int>)
                    "Byte" -> data.byte.set(name, value as Byte)
                    "Float" -> data.float.set(name, value as Float)
                    "Double" -> data.double.set(name, value as Double)
                    "Short" -> data.short.set(name, value as Short)
                    "Long" -> data.long.set(name, value as Long)
                    "ByteArray" -> data.bytes.set(name, value as List<Byte>)
                }
            }
        }
        return data
    }
}

fun <T : NBTSerializable> T.fromNBT(data: NBT): T {
    val clazz = this::class
    clazz.memberProperties.forEach {
        val tag = it.findAnnotation<NBTTag>()
        if(tag != null) {
            val tagname = tag.name
            val fieldname = it.name
            val dataType = it.returnType.toString().replace("kotlin.","")

            val property = this::class.memberProperties.find { it.name == fieldname } as KMutableProperty<*>

            when(dataType) {
                "String" -> { property.setter.call(this, data.string.get(tagname)) }
                "Int" -> { property.setter.call(this, data.int.get(tagname)) }
                "IntArray" -> { property.setter.call(this, data.ints.get(tagname)) }
                "Byte" -> { property.setter.call(this, data.byte.get(tagname)) }
                "Float" -> { property.setter.call(this, data.float.get(tagname)) }
                "Double" -> { property.setter.call(this, data.double.get(tagname)) }
                "Short" -> { property.setter.call(this, data.short.get(tagname)) }
                "Long" -> { property.setter.call(this, data.long.get(tagname)) }
                "ByteArray" -> { property.setter.call(this, data.bytes.get(tagname)) }
            }
        }
    }

    return this
}