package com.gh0u1l5.wechatmagician.spellbook.base

import com.gh0u1l5.wechatmagician.spellbook.util.BasicUtil.tryAsynchronously
import com.gh0u1l5.wechatmagician.spellbook.util.BasicUtil.tryVerbosely
import com.gh0u1l5.wechatmagician.spellbook.util.XposedUtil
import de.robv.android.xposed.XC_MethodHook
import java.util.concurrent.ConcurrentHashMap

abstract class EventCenter: HookerProvider {

    abstract val interfaces: List<Class<*>>

    private val registries: MutableMap<String, Set<Any>> = ConcurrentHashMap()

    private fun Any.hasEvent(event: String) =
            this::class.java.declaredMethods.any { it.name == event }

    private fun register(event: String, observer: Any) {
        if (observer.hasEvent(event)) {
            val hooker = provideEventHooker(event)
            if (hooker != null && !hooker.hasHooked) {
                XposedUtil.postHooker(hooker)
            }
            val added = registries[event] ?: emptySet()
            registries[event] = added + observer
        }
    }

    fun register(`interface`: Class<*>, observer: Any) {
        `interface`.methods.forEach { method ->
            register(method.name, observer)
        }
    }

    fun notify(event: String, action: (Any) -> Unit) {
        if (event.isEmpty()) {
            throw IllegalArgumentException("event cannot be empty!")
        }
        registries[event]?.forEach {
            tryVerbosely { action(it) }
        }
    }

    fun notifyParallel(event: String, action: (Any) -> Unit) {
        if (event.isEmpty()) {
            throw IllegalArgumentException("event cannot be empty!")
        }
        registries[event]?.map { observer ->
            tryAsynchronously { action(observer) }
        }?.forEach(Thread::join)
    }

    /**
     * If the hooked method has no return type, then the action may only decide whether interrupt it or not.
     */
    fun notifyWithInterrupt(event: String, param: XC_MethodHook.MethodHookParam, action: (Any) -> Boolean) {
        if (event.isEmpty()) {
            throw IllegalArgumentException("event cannot be empty!")
        }
        registries[event]?.forEach {
            tryVerbosely {
                val shouldInterrupt = action(it)
                if (shouldInterrupt) {
                    param.result = null
                }
            }
        }
    }

    /**
     * If the hooked method has a return type, then the action may have an general operation.
     */
    fun notifyWithOperation(event: String, param: XC_MethodHook.MethodHookParam, action: (Any) -> Operation<*>) {
        if (event.isEmpty()) {
            throw IllegalArgumentException("event cannot be empty!")
        }
        var priority = -1
        registries[event]?.forEach {
            tryVerbosely {
                val ret = action(it)
                if (ret.returnEarly && ret.priority > priority) {
                    param.result = ret.value
                    priority = ret.priority
                }
            }
        }
    }

    fun <T: Any>notifyForResult(event: String, action: (Any) -> T?): List<T> {
        if (event.isEmpty()) {
            throw IllegalArgumentException("event cannot be empty!")
        }
        return registries[event]?.mapNotNull {
            tryVerbosely { action(it) }
        } ?: emptyList()
    }
}