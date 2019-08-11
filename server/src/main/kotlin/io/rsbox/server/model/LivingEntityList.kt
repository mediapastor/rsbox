package io.rsbox.server.model

import io.rsbox.server.model.entity.LivingEntity

/**
 * @author Kyle Escobar
 */

class LivingEntityList<T : LivingEntity>(private val list: Array<T?>) {
    val entityList: Array<T?> = list

    val capacity = list.size

    private var count = 0

    operator fun get(index: Int): T? = list[index]

    fun contains(entity: T): Boolean = list[entity.index] == entity

    fun count(): Int = count

    fun count(predicate: (T) -> Boolean): Int {
        var count = 0
        for(element in list) {
            if(element != null && predicate(element)) {
                count++
            }
        }
        return count
    }

    fun add(entity: T): Boolean {
        for(i in 1 until list.size) {
            if(list[i] == null) {
                list[i] = entity
                entity.index = i
                count++
                return true
            }
        }
        return false
    }

    fun remove(entity: T): Boolean {
        if(list[entity.index] == entity) {
            list[entity.index] = null
            entity.index = -1
            count--
            return true
        }

        return false
    }

    fun forEach(action: (T) -> Unit) {
        for(element in list) {
            if(element != null) {
                action(element)
            }
        }
    }
}