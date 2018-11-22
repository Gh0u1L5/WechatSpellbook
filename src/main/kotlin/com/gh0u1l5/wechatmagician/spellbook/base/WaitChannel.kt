package com.gh0u1l5.wechatmagician.spellbook.base

class WaitChannel {
    @Volatile private var done = false
    private val channel = java.lang.Object()

    private val current: Long
        get() = System.currentTimeMillis()

    fun wait(timeout: Long = 0L): Boolean {
        if (done) return false

        val start = current
        synchronized(channel) {
            // Handle spurious wakeup.
            while (!done && start + timeout > current) {
                channel.wait(start + timeout - current)
            }
            return true
        }
    }

    fun done() {
        synchronized(channel) {
            done = true
            channel.notifyAll()
        }
    }

    fun isDone() = synchronized(channel) { done }
}