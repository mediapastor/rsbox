package io.rsbox.server.net.login

import io.netty.channel.Channel

/**
 * @author Kyle Escobar
 */

data class LoginRequest(
    val channel: Channel,
    val username: String,
    val password: String,
    val revision: Int,
    val xteaKeys: IntArray,
    val resizableClient: Boolean,
    val clientWidth: Int,
    val clientHeight: Int,
    val authCode: Int,
    val uuid: String,
    val reconnecting: Boolean
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LoginRequest

        if (channel != other.channel) return false
        if (username != other.username) return false
        if (password != other.password) return false
        if (revision != other.revision) return false
        if (!xteaKeys.contentEquals(other.xteaKeys)) return false
        if (resizableClient != other.resizableClient) return false
        if (clientWidth != other.clientWidth) return false
        if (clientHeight != other.clientHeight) return false
        if (authCode != other.authCode) return false
        if (uuid != other.uuid) return false
        if (reconnecting != other.reconnecting) return false

        return true
    }

    override fun hashCode(): Int {
        var result = channel.hashCode()
        result = 31 * result + username.hashCode()
        result = 31 * result + (password.hashCode())
        result = 31 * result + revision
        result = 31 * result + xteaKeys.contentHashCode()
        result = 31 * result + resizableClient.hashCode()
        result = 31 * result + clientWidth
        result = 31 * result + clientHeight
        result = 31 * result + authCode
        result = 31 * result + uuid.hashCode()
        result = 31 * result + reconnecting.hashCode()
        return result
    }
}