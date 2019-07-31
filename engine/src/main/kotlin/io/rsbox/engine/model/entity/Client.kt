package io.rsbox.engine.model.entity

import io.netty.channel.Channel

/**
 * @author Kyle Escobar
 */

class Client(val channel: Channel) : Player() {

}