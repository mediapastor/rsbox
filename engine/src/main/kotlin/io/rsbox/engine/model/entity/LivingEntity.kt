package io.rsbox.engine.model.entity

import io.rsbox.api.serialization.nbt.NBTSerializable

/**
 * @author Kyle Escobar
 */

open class LivingEntity : NBTSerializable() {
    var index: Int = -1
}