package com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.wcdb.support

import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxLazy
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxLoader
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.wcdb.Package.WECHAT_PACKAGE_SQLITE
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil.findClassIfExists

object Classes {
    val SQLiteCancellationSignal: Class<*> by wxLazy("SQLiteCancellationSignal") {
        findClassIfExists("$WECHAT_PACKAGE_SQLITE.support.CancellationSignal", wxLoader!!)
    }
}