package com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.booter.notification.queue

import com.gh0u1l5.wechatmagician.spellbook.C
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxLazy
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.booter.notification.Classes.NotificationItem
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.booter.notification.queue.Classes.NotificationAppMsgQueue
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil.findMethodsByExactParameters
import java.lang.reflect.Method

object Methods {
    val NotificationAppMsgQueue_add: Method by wxLazy("NotificationAppMsgQueue_add") {
        findMethodsByExactParameters(NotificationAppMsgQueue, null, NotificationItem)
                .firstOrNull()?.apply { isAccessible = true }
    }
    val NotificationAppMsgQueue_remove: Method by wxLazy("NotificationAppMsgQueue_remove") {
        findMethodsByExactParameters(NotificationAppMsgQueue, C.Boolean, C.String)
                .firstOrNull()?.apply { isAccessible = true }
    }
}