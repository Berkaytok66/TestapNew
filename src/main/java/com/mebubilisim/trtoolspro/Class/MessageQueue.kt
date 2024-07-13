package com.mebubilisim.trtoolspro.Class

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object MessageQueue {
    private val messageQueue = mutableListOf<() -> Unit>()
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        processMessageQueue()
    }

    fun queueMessage(message: () -> Unit) {
        synchronized(messageQueue) {
            messageQueue.add(message)
        }
    }

    private fun processMessageQueue() {
        scope.launch {
            while (true) {
                val message = synchronized(messageQueue) {
                    if (messageQueue.isNotEmpty()) messageQueue.removeAt(0) else null
                }
                message?.invoke()
                delay(500) // Her mesaj arasında 500ms gecikme
            }
        }
    }
}
