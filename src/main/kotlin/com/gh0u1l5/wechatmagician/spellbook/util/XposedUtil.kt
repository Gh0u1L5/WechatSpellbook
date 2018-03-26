package com.gh0u1l5.wechatmagician.spellbook.util

import android.os.Handler
import android.os.HandlerThread
import com.gh0u1l5.wechatmagician.spellbook.base.Hooker
import com.gh0u1l5.wechatmagician.spellbook.util.BasicUtil.tryVerbosely

object XposedUtil {

    private val hookerHandlerThread = HandlerThread("HookerHandler").apply { start() }
    private val hookerHandler: Handler = Handler(hookerHandlerThread.looper)

    fun postHooker(hooker: Hooker) {
        hookerHandler.post {
            if (!hooker.hasHooked) {
                tryVerbosely {
                    hooker.hook()
                    hooker.hasHooked = true
                }
            }
        }
    }
}