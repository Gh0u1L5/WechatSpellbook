package com.gh0u1l5.wechatmagician.spellbook.base

/**
 * 封装一个 Xposed 下的 Hook 操作
 *
 * @param hook 实际向 Xposed 框架注册钩子的回调函数
 */
data class Hooker(val hook: () -> Unit) {
    /**
     * 用来防止重复 Hook 的标记
     */
    @Volatile var hasHooked = false
}