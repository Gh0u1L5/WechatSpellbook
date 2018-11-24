package com.gh0u1l5.wechatmagician.spellbook.base

/**
 * 当插件监听到某个事件发生, 并拦截到相应的函数调用的时候, 插件可能会需要对拦截住的函数进行某些操作, 这个操作需要被封
 * 装成一个 [Operation] 对象传递给 SpellBook 框架
 */
class Operation<out T>(
        val value: T? = null,
        val error: Throwable? = null,
        val priority: Int = 0,
        val returnEarly: Boolean = false
) {
    companion object {
        /**
         * 创建一个空操作, 表明自己什么也不做
         */
        @JvmStatic fun <T>nop(priority: Int = 0): Operation<T> {
            return Operation(priority = priority)
        }

        /**
         * 创建一个打断操作, 跳过原函数的执行, 直接抛出一个异常
         *
         * @param error 要抛出的异常
         * @param priority 操作的优先级, 当多个插件同时做出操作的时候, 框架将选取优先级较高的结果, 优先级相同的
         * 情况下随机选择一个操作
         */
        @JvmStatic fun <T>interruption(error: Throwable, priority: Int = 0): Operation<T> {
            return Operation(error = error, priority = priority, returnEarly = true)
        }

        /**
         * 创建一个替换操作, 跳过原函数的执行, 直接返回一个结果
         *
         * @param value 要返回的结果
         * @param priority 操作的优先级, 当多个插件同时做出操作的时候, 框架将选取优先级较高的结果, 优先级相同的
         * 情况下随机选择一个操作
         */
        @JvmStatic fun <T>replacement(value: T, priority: Int = 0): Operation<T> {
            return Operation(value = value, priority = priority, returnEarly = true)
        }
    }
}