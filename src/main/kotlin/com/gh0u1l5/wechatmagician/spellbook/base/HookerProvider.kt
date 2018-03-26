package com.gh0u1l5.wechatmagician.spellbook.base

interface HookerProvider {
    fun provideStaticHookers(): List<Hooker>? = null
    fun provideEventHooker(event: String): Hooker? = null
}