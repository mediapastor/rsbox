package io.rsbox.engine.model.entity

import io.netty.channel.Channel
import io.rsbox.api.entity.Client

/**
 * @author Kyle Escobar
 */

class RSClient(val channel: Channel) : Player(), Client {
}