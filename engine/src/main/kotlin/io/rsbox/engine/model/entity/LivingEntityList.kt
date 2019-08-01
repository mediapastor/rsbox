package io.rsbox.engine.model.entity

/**
 * @author Kyle Escobar
 */

class LivingEntityList<T: LivingEntity>(private val entities: Array<T?>) {
    val entries: Array<T?> = entities

    val capacity = entities.size

    private var count = 0

    operator fun get(index: Int): T? = entities[index]

    fun contains(livingEntity: T): Boolean = entities[livingEntity.index] == livingEntity

    fun count(): Int = count

    fun add(livingEntity: T): Boolean {
        for(i in 1 until entities.size) {
            if(entities[i] == null) {
                entities[i] = livingEntity
                livingEntity.index = i
                count++
                return true
            }
        }
        return false
    }

    fun remove(livingEntity: T): Boolean {
        if(entities[livingEntity.index] == livingEntity) {
            entities[livingEntity.index] = null
            livingEntity.index = -1
            count--
            return true
        }

        return false
    }

    fun remove(index: Int): T? {
        if(entities[index] != null) {
            val livingEntity = entities[index]
            entities[index] = null
            count--
            return livingEntity
        }
        return null
    }
}