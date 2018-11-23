package com.gh0u1l5.wechatmagician.spellbook.util

import android.util.Log
import kotlin.concurrent.thread

/**
 * BasicUtil contains the helper functions for general purpose.
 */
object BasicUtil {
    /**
     * Executes a callback and ignore any thrown exceptions.
     */
    inline fun <T: Any>trySilently(func: () -> T?): T? {
        return try { func() } catch (t: Throwable) { null }
    }

    /**
     * Executes a callback and record any thrown exceptions in the Xposed log.
     */
    inline fun <T: Any>tryVerbosely(func: () -> T?): T? {
        return try { func() } catch (t: Throwable) {
            Log.e("Xposed", Log.getStackTraceString(t)); null
        }
    }

    /**
     * Executes a callback asynchronously and record any thrown exceptions in the Xposed log.
     *
     * Remember to handle UI operations in UI thread properly in the callback.
     */
    inline fun tryAsynchronously(crossinline func: () -> Unit): Thread {
        return thread(start = true) { func() }.apply {
            setUncaughtExceptionHandler { _, t ->
                Log.e("Xposed", Log.getStackTraceString(t))
            }
        }
    }
}