package com.gh0u1l5.wechatmagician.spellbook.util

import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import com.gh0u1l5.wechatmagician.spellbook.base.Hooker
import com.gh0u1l5.wechatmagician.spellbook.util.BasicUtil.trySilently
import com.gh0u1l5.wechatmagician.spellbook.util.BasicUtil.tryVerbosely

/**
 * 封装了一批用于和 Xposed 框架通信的方法
 */
object XposedUtil {
    /**
     * 用于处理 Hook 任务的线程池
     */
    private val workerPool = ParallelUtil.createThreadPool()

    /**
     * 用于分发 Hook 任务的管理线程
     */
    private val managerThread = HandlerThread("HookHandler").apply { start() }

    /**
     * 用于分发 Hook 任务的 [Handler]
     */
    private val managerHandler: Handler = Handler(managerThread.looper)

    /**
     * 依据当前系统的版本选择合适的 Hook 策略
     *
     * @param hook 用于向 Xposed 框架注册事件的回调函数
     */
    @JvmStatic private inline fun tryHook(crossinline hook: () -> Unit) {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                /**
                 * WARN: 对于 Android 7.x 及以上, Xposed 多线程 HOOK 会导致崩溃.
                 */
                tryVerbosely(hook)
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                workerPool.execute { tryVerbosely(hook) }
            }
            else -> {
                /**
                 * WARN: 对于 Android 4.x 及以下, MultiDex 的支持还不完善, 日志中会出现大量误报.
                 */
                workerPool.execute { trySilently(hook) }
            }
        }
    }

    /**
     * 将 [Hooker] 对象发送给管理线程, 等待进一步的处理
     */
    @JvmStatic fun postHooker(hooker: Hooker) {
        managerHandler.post {
            tryHook { hooker.hook() }
        }
    }
}