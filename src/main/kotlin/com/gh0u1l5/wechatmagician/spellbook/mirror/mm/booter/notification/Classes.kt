package com.gh0u1l5.wechatmagician.spellbook.mirror.mm.booter.notification

import com.gh0u1l5.wechatmagician.spellbook.C
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxClasses
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxLazy
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxLoader
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxPackageName
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil.findClassIfExists
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil.findClassesFromPackage

object Classes {
    private val classesInCurrentPackage by wxLazy("$wxPackageName.booter.notification") {
        findClassesFromPackage(wxLoader!!, wxClasses!!, "$wxPackageName.booter.notification")
    }

    val MMNotification: Class<*> by wxLazy("MMNotification") {
        classesInCurrentPackage
                .filterByMethod(null, "notify", C.Int, C.Notification)
                .firstOrNull()
    }

    val MMNotification_MessageHandler: Class<*> by wxLazy("MMNotification_MessageHandler") {
        classesInCurrentPackage
                .filterByMethod(null, "handleMessage", C.Message)
                .firstOrNull()
    }

    val NotificationItem: Class<*> by wxLazy("NotificationItem") {
        findClassIfExists("$wxPackageName.booter.notification.NotificationItem", wxLoader)
    }
}