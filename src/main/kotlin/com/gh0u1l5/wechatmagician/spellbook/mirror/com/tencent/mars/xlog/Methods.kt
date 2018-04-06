package com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mars.xlog

import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxLazy
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mars.xlog.Classes.Xlog
import java.lang.reflect.Method

object Methods {
    val Xlog_logWrite: Method by wxLazy("Xlog_logWrite") {
        Xlog.declaredMethods.find { it.name == "logWrite" }
    }

    val Xlog_logWrite2: Method by wxLazy("Xlog_logWrite2") {
        Xlog.declaredMethods.find { it.name == "logWrite2" }
    }
}