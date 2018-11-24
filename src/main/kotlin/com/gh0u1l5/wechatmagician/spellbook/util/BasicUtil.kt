package com.gh0u1l5.wechatmagician.spellbook.util

import android.util.Log
import kotlin.concurrent.thread

/**
 * 封装了一批很便利的常用操作
 */
object BasicUtil {
    /**
     * 执行回调函数, 无视它抛出的任何异常
     */
    @JvmStatic inline fun <T: Any>trySilently(func: () -> T?): T? {
        return try { func() } catch (t: Throwable) { null }
    }

    /**
     * 执行回调函数, 将它抛出的异常记录到 Xposed 的日志里
     */
    @JvmStatic inline fun <T: Any>tryVerbosely(func: () -> T?): T? {
        return try { func() } catch (t: Throwable) {
            Log.e("Xposed", Log.getStackTraceString(t)); null
        }
    }

    /**
     * 异步执行回调函数, 将它抛出的记录到 Xposed 的日志里
     *
     * WARN: 别忘了任何 UI 操作都必须使用 runOnUiThread
     */
    @JvmStatic inline fun tryAsynchronously(crossinline func: () -> Unit): Thread {
        return thread(start = true) { func() }.apply {
            setUncaughtExceptionHandler { _, t ->
                Log.e("Xposed", Log.getStackTraceString(t))
            }
        }
    }
}