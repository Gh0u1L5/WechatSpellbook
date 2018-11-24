package com.gh0u1l5.wechatmagician.spellbook.base

/**
 * 将一次 Hook 操作封装成对象, 防止对同样的函数反复下钩, 造成难以调查的BUG
 *
 * 这个类是线程安全的, 多个线程同时调用只会有一个线程成功下钩
 *
 * @property hooker 实际向 Xposed 框架注册钩子的回调函数
 * @constructor 将一次 Hook 操作封装成一个 Hooker 对象
 */
class Hooker(private val hooker: () -> Unit) {
    /**
     * 用来防止重复 Hook 的标记
     */
    var hasHooked = false
        private set

    /**
     * 尝试执行一次 Hook 操作, 如果已经钩过了就不再重复
     */
    @Synchronized fun hook() {
        if (!hasHooked) {
            hooker()
            hasHooked = true
        }
    }
}