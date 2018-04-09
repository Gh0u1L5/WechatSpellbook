package com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mars.xlog

import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxLazy
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxLoader
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil.findClassIfExists

object Classes {
    val Xlog: Class<*> by wxLazy("Xlog") {
        findClassIfExists("com.tencent.mars.xlog.Xlog", wxLoader!!)
    }
}