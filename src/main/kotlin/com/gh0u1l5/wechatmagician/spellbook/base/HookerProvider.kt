package com.gh0u1l5.wechatmagician.spellbook.base

/**
 * 一个 HookerProvider 可以向 SpellBook 框架提供自定义的钩子, 框架将根据具体的情况将这些钩子注册到 Xposed 框架里
 */
interface HookerProvider {
    /**
     * 返回一组静态钩子, 即传统的 Xposed 钩子
     *
     * 不论发生啥事, 这些钩子都会被注册到 Xposed 里
     */
    fun provideStaticHookers(): List<Hooker>? = null

    /**
     * 返回一个针对具体事件进行监听的钩子
     *
     * 只有在某个插件要求监听某个事件的情况下, 对应的钩子才会被注册, 在目前的版本中, 只有 [EventCenter] 才需要实
     * 现该方法
     *
     * WARN: 对于同一个事件, 请返回相同的 Hooker 对象, 这样可以有效避免重复的 hook 行为, 根除潜在的 Bug
     */
    fun provideEventHooker(event: String): Hooker? = null
}