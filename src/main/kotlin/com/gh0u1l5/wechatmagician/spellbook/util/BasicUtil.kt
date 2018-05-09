package com.gh0u1l5.wechatmagician.spellbook.util

import android.util.Log
import kotlin.concurrent.thread

/**
 * BasicUtil contains the helper functions for general purpose.
 */
object BasicUtil {
    /**
     * trySilently will execute a callback and ignore any thrown exceptions.
     */
    fun <T: Any>trySilently(func: () -> T?): T? {
        return try { func() } catch (t: Throwable) { null }
    }

    /**
     * tryVerbosely will execute a callback and record any thrown exceptions to the Xposed log.
     */
    fun <T: Any>tryVerbosely(func: () -> T?): T? {
        return try { func() } catch (t: Throwable) {
            Log.e("Xposed", Log.getStackTraceString(t)); null
        }
    }

    /**
     * tryAsynchronously will execute a callback in another thread and record any thrown exceptions
     * to the Xposed log.
     *
     * Remember to handle UI operations in UI thread properly in the callback.
     */
    fun tryAsynchronously(func: () -> Unit): Thread {
        return thread(start = true) { func() }.apply {
            setUncaughtExceptionHandler { _, t ->
                Log.e("Xposed", Log.getStackTraceString(t))
            }
        }
    }
}