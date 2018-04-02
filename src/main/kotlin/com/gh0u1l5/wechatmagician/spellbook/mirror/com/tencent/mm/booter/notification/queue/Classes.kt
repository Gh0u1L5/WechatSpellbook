package com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.booter.notification.queue

import com.gh0u1l5.wechatmagician.spellbook.C
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxClasses
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxLazy
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxLoader
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxPackageName
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.booter.notification.Classes.NotificationItem
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil.findClassesFromPackage

object Classes {
    val NotificationAppMsgQueue: Class<*> by wxLazy("NotificationAppMsgQueue") {
        findClassesFromPackage(wxLoader!!, wxClasses!!, "$wxPackageName.booter.notification.queue")
                .filterByMethod(null, NotificationItem)
                .filterByMethod(C.Iterator, "iterator")
                .firstOrNull()
    }
}