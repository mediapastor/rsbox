package io.rsbox.server.net.event

import io.rsbox.server.net.NetworkSession

/**
 * @author Kyle Escobar
 */

abstract class NetworkWriteEvent(val session: NetworkSession, val ctx1: Any?, val ctx2: Any? = null) : Runnable {

    override fun run() {
        try {
            if(ctx2 == null) {
                write(session, ctx1!!)
            } else {
                write(session, ctx1!!, ctx2)
            }
        } catch(e: Throwable) {

        }
    }


    abstract fun write(session: NetworkSession, ctx: Any)

    abstract fun write(session: NetworkSession, ctx1: Any, ctx2: Any)
}