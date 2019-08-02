package io.rsbox.engine.serialization.nbt

import io.rsbox.engine.Server
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

/**
 * @author Kyle Escobar
 */

@Suppress("UNCHECKED_CAST")
interface NBTSerializable {
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
                    "IntArray" -> data.ints.set(name, (value as IntArray).toList())
                    "Byte" -> data.byte.set(name, value as Byte)
                    "Float" -> data.float.set(name, value as Float)
                    "Double" -> data.double.set(name, value as Double)
                    "Short" -> data.short.set(name, value as Short)
                    "Long" -> data.long.set(name, value as Long)
                    "ByteArray" -> data.bytes.set(name, value as List<Byte>)
                    else -> {
                        val compoundClass = Class.forName(dataType)
                        if(NBTCustomType::class.java.isAssignableFrom(compoundClass)) {
                            val customValueNbt = (value as NBTCustomType).toNBT()
                            data.nbt.set(name,customValueNbt)
                        } else {
                            Server.logger.info { "Unable to serialize field ${name} with datatype ${dataType} as it does not implement 'NBTCustomType'. " }
                        }
                    }
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
                "String" -> { if(data.string[tagname] != null) property.setter.call(this, data.string[tagname]) }
                "Int" -> { if(data.int[tagname] != null) property.setter.call(this, data.int[tagname]) }
                "IntArray" -> { if(data.ints[tagname] != null) property.setter.call(this, data.ints[tagname]!!.toIntArray()) }
                "Byte" -> { if(data.byte[tagname] != null) property.setter.call(this, data.byte[tagname]) }
                "Float" -> { if(data.float[tagname] != null) property.setter.call(this, data.float[tagname]) }
                "Double" -> { if(data.double[tagname] != null) property.setter.call(this, data.double[tagname]) }
                "Short" -> { if(data.short[tagname] != null) property.setter.call(this, data.short[tagname]) }
                "Long" -> { if(data.long[tagname] != null) property.setter.call(this, data.long[tagname]) }
                "ByteArray" -> { if(data.bytes[tagname] != null) property.setter.call(this, data.bytes[tagname]) }
                else -> {
                    val genericType = parseSubNBT<NBTCustomType>(tagname,dataType, data)
                    property.setter.call(this, genericType)
                }
            }
        }
    }

    return this
}

@Suppress("UNCHECKED_CAST")
private fun <T : NBTCustomType> parseSubNBT(tagname: String, dataType: String, data: NBT): T? {
    val clazz: Class<*> = Class.forName(dataType)
    if(NBTCustomType::class.java.isAssignableFrom(clazz)) {
        val obj: T = clazz.newInstance() as T
        val subnbt = data.nbt.get(tagname)
        obj.fromNBT(subnbt)
        return obj
    } else {
        return null
    }
}