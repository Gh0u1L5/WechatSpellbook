package com.gh0u1l5.wechatmagician.spellbook.base

data class Hooker(val hook: () -> Unit) {
    @Volatile var hasHooked = false
}