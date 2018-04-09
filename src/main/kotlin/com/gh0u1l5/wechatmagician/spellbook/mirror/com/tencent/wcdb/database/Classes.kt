package com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.wcdb.database

import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxLazy
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxLoader
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.wcdb.Package.WECHAT_PACKAGE_SQLITE
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil.findClassIfExists

object Classes {
    val SQLiteDatabase: Class<*> by wxLazy("SQLiteDatabase") {
        findClassIfExists("$WECHAT_PACKAGE_SQLITE.database.SQLiteDatabase", wxLoader!!)
    }
    val SQLiteCursorFactory: Class<*> by wxLazy("SQLiteCursorFactory") {
        findClassIfExists("$WECHAT_PACKAGE_SQLITE.database.SQLiteDatabase\$CursorFactory", wxLoader!!)
    }
}