package com.gh0u1l5.wechatmagician.spellbook.util

import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import com.gh0u1l5.wechatmagician.spellbook.base.Hooker
import com.gh0u1l5.wechatmagician.spellbook.util.BasicUtil.tryAsynchronously
import com.gh0u1l5.wechatmagician.spellbook.util.BasicUtil.tryVerbosely

object XposedUtil {

    private val hookerHandlerThread = HandlerThread("HookerHandler").apply { start() }
    private val hookerHandler: Handler = Handler(hookerHandlerThread.looper)

    /**
     * Hooks functions in suitable strategy for corresponding API levels. NOTE: for Android 7.X or
     * later, multi-thread causes unexpected crashes with WeXposed, so we drop this feature for now.
     *
     * @param hook the callback function that actually hook functions using Xposed.
     */
    private fun tryHook(hook: () -> Unit) {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> tryVerbosely { hook() }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> tryAsynchronously { hook() }
            else -> tryAsynchronously { try { hook() } catch (t: Throwable) { /* Ignore */ } }
        }
    }

    fun postHooker(hooker: Hooker) {
        hookerHandler.post {
            tryHook {
                synchronized(hooker) {
                    if (hooker.hasHooked) {
                        return@tryHook
                    }
                    hooker.hook()
                    hooker.hasHooked = true
                }
            }
        }
    }
}