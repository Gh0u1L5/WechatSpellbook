package com.gh0u1l5.wechatmagician.spellbook.base

import com.gh0u1l5.wechatmagician.spellbook.util.BasicUtil.tryAsynchronously
import com.gh0u1l5.wechatmagician.spellbook.util.BasicUtil.tryVerbosely
import com.gh0u1l5.wechatmagician.spellbook.util.XposedUtil
import de.robv.android.xposed.XC_MethodHook
import java.util.concurrent.ConcurrentHashMap

abstract class EventCenter: HookerProvider {

    abstract val interfaces: List<Class<*>>

    private val observers: MutableMap<String, Set<Any>> = ConcurrentHashMap()

    private fun Any.hasEvent(event: String) =
            this::class.java.declaredMethods.any { it.name == event }

    private fun register(event: String, observer: Any) {
        if (observer.hasEvent(event)) {
            val hooker = provideEventHooker(event)
            if (hooker != null && !hooker.hasHooked) {
                XposedUtil.postHooker(hooker)
            }
            val existing = observers[event] ?: emptySet()
            observers[event] = existing + observer
        }
    }

    fun register(`interface`: Class<*>, observer: Any) {
        `interface`.methods.forEach { method ->
            register(method.name, observer)
        }
    }

    /**
     * Notify all the observers who is watching an event by applying an action on each of them.
     *
     * @param event the event those observers are watching
     * @param action the actual codes which notify the observers
     */
    fun notify(event: String, action: (Any) -> Unit) {
        observers[event]?.forEach {
            tryVerbosely { action(it) }
        }
    }

    /**
     * The asynchronous version of [notify] method.
     */
    fun notifyParallel(event: String, action: (Any) -> Unit) {
        observers[event]?.map { observer ->
            tryAsynchronously { action(observer) }
        }?.forEach(Thread::join)
    }

    /**
     * Notify all the observers who is watching an event by applying an action on each of them, and
     * then collect the results returned by the action function.
     *
     * @param event the event those observers are watching
     * @param action the actual codes which notify the observers and return a result for later use.
     */
    fun <T: Any>notifyForResults(event: String, action: (Any) -> T?): List<T> {
        return observers[event]?.mapNotNull {
            tryVerbosely { action(it) }
        } ?: emptyList()
    }

    /**
     * Notify all the observers who is watching an event by applying an action on each of them, and
     * then collect the boolean flags to determine whether the original method should be bypassed.
     * If any one of the observers claim that the hooked method should be bypassed, then it will be
     * bypassed.
     *
     * @param event the event those observers are watching
     * @param param the [XC_MethodHook.MethodHookParam] object that allow us to bypass the method.
     * @param default the default return value for the bypassed method.
     * @param action the actual codes which notify the observers and return a bypass flag.
     */
    fun notifyForBypassFlags(event: String, param: XC_MethodHook.MethodHookParam, default: Any? = null, action: (Any) -> Boolean) {
        val shouldBypass = notifyForResults(event, action).any()
        if (shouldBypass) {
            param.result = default
        }
    }

    /**
     * Notify all the observers who is watching an event by applying an action on each of them, and
     * then collect the returned operations to determine how to handle the hooked method.
     *
     * @param event the event those observers are watching
     * @param param the [XC_MethodHook.MethodHookParam] object that allow us to handle the method.
     * @param action the actual codes which notify the observers and return a general operation.
     */
    fun notifyForOperations(event: String, param: XC_MethodHook.MethodHookParam, action: (Any) -> Operation<*>) {
        val operations = notifyForResults(event, action)
        val result = operations.filter { it.returnEarly }.maxBy { it.priority }
        if (result != null) {
            if (result.value != null) {
                param.result = result.value
            }
            if (result.error != null) {
                param.throwable = result.error
            }
        }
    }
}