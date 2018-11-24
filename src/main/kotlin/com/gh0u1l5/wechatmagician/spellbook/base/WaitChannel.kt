package com.gh0u1l5.wechatmagician.spellbook.base

/**
 * 用 Java 实现的一个安全的 Wait Channel, 用来让若干线程安全地阻塞到事件结束
 */
class WaitChannel {
    @Volatile private var done = false
    private val channel = java.lang.Object()

    private val current: Long
        get() = System.currentTimeMillis()

    fun wait(timeout: Long = 0L): Boolean {
        if (done) return false

        val start = current
        synchronized(channel) {
            // 处理可能的 spurious wakeup
            while (!done && start + timeout > current) {
                channel.wait(start + timeout - current)
            }
            return true
        }
    }

    fun done() {
        if (done) return

        synchronized(channel) {
            done = true
            channel.notifyAll()
        }
    }

    fun isDone() = done
}