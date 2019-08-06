package io.rsbox.server.net

import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel

/**
 * @author Kyle Escobar
 */

class ServerSocketConnection(var selector: Selector, var channel: ServerSocketChannel) {

}