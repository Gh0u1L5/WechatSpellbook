package com.gh0u1l5.wechatmagician.spellbook.base

import com.gh0u1l5.wechatmagician.spellbook.util.BasicUtil.tryVerbosely
import com.gh0u1l5.wechatmagician.spellbook.util.ParallelUtil.parallelForEach
import com.gh0u1l5.wechatmagician.spellbook.util.XposedUtil
import de.robv.android.xposed.XC_MethodHook
import java.util.concurrent.ConcurrentHashMap

/**
 * SpellBook 框架的事件中心, 用于提供标准化的事件通知
 */
abstract class EventCenter: HookerProvider {

    /**
     * 事件中心所支持的接口列表, 任何想要注册到该中心的插件必须实现其中起码一个接口
     */
    abstract val interfaces: List<Class<*>>

    /**
     * 不同事件所对应的观察者列表
     */
    private val observers: MutableMap<String, Set<Any>> = ConcurrentHashMap()

    /**
     * 判断指定插件对象是否关注了某个事件
     *
     * "关注" 的判断标准是这个对象有没有直接实现一个和事件同名的方法, 从基类继承的方法不算在内
     */
    private fun Any.hasEvent(event: String) =
            this::class.java.declaredMethods.any { it.name == event }

    /**
     * 向事件中心注册一个观察者
     *
     * @param event 观察者关注的事件
     * @param observer 观察者本人
     */
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

    /**
     * 向事件中心注册一个插件
     *
     * @param inface 该插件所实现的接口, 必须为 [interfaces] 中存在的接口
     * @param plugin 插件对象
     */
    fun register(inface: Class<*>, plugin: Any) {
        inface.methods.forEach { method ->
            register(method.name, plugin)
        }
    }

    /**
     * 找到关注某个事件的所有观察者, 若不存在则返回 null
     */
    fun findObservers(event: String): Set<Any>? = observers[event]

    /**
     * 通知所有正在观察某个事件的观察者
     *
     * @param event 具体发生的事件
     * @param action 对观察者进行通知的回调函数
     */
    inline fun notify(event: String, action: (Any) -> Unit) {
        findObservers(event)?.forEach {
            tryVerbosely { action(it) }
        }
    }

    /**
     * 通知所有正在观察某个事件的观察者（并行计算版本）
     *
     * @param event 具体发生的事件
     * @param action 对观察者进行通知的回调函数
     */
    inline fun notifyParallel(event: String, crossinline action: (Any) -> Unit) {
        findObservers(event)?.parallelForEach { observer ->
            tryVerbosely { action(observer) }
        }
    }

    /**
     * 通知所有正在观察某个事件的观察者, 并收集它们的反馈
     *
     * @param event 具体发生的事件
     * @param action 对观察者进行通知的回调函数
     */
    inline fun <T: Any>notifyForResults(event: String, action: (Any) -> T?): List<T> {
        return findObservers(event)?.mapNotNull {
            tryVerbosely { action(it) }
        } ?: emptyList()
    }

    /**
     * 通知所有正在观察某个事件的观察者, 并收集它们的反馈, 以确认是否需要拦截该事件
     *
     * 如果有任何一个观察者返回了 true, 我们就认定当前事件是一个需要被拦截的事件. 例如当微信写文件的时候, 某个观察者
     * 检查过文件路径后返回了 true, 那么框架就会拦截这次写文件操作, 向微信返回一个默认值
     *
     * @param event 具体发生的事件
     * @param param 拦截函数调用后得到的 [XC_MethodHook.MethodHookParam] 对象
     * @param default 跳过函数调用之后, 仍然需要向 caller 提供一个返回值
     * @param action 对观察者进行通知的回调函数
     */
    inline fun notifyForBypassFlags(event: String, param: XC_MethodHook.MethodHookParam, default: Any? = null, action: (Any) -> Boolean) {
        val shouldBypass = notifyForResults(event, action).any()
        if (shouldBypass) {
            param.result = default
        }
    }

    /**
     * 通知所有正在观察某个事件的观察者, 并收集它们的反馈, 以确认该对这次事件采取什么操作
     *
     * 在获取了观察者建议的操作之后, 我们会对这些操作的优先级进行排序, 从优先级最高的操作中选择一个予以采纳
     *
     * @param event 具体发生的事件
     * @param param 拦截函数调用后得到的 [XC_MethodHook.MethodHookParam] 对象
     * @param action 对观察者进行通知的回调函数
     */
    inline fun notifyForOperations(event: String, param: XC_MethodHook.MethodHookParam, action: (Any) -> Operation<*>) {
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