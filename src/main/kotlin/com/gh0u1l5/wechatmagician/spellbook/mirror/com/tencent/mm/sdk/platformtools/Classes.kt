package com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.sdk.platformtools

import com.gh0u1l5.wechatmagician.spellbook.C
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxClasses
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxLazy
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxLoader
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxPackageName
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil.findClassesFromPackage

object Classes {
    val Logcat: Class<*> by wxLazy("Logcat") {
        findClassesFromPackage(wxLoader!!, wxClasses!!, "$wxPackageName.sdk.platformtools")
                .filterByEnclosingClass(null)
                .filterByMethod(C.Int, "getLogLevel")
                .firstOrNull()
    }

    val LruCache: Class<*> by wxLazy("LruCache") {
        findClassesFromPackage(wxLoader!!, wxClasses!!, "$wxPackageName.sdk.platformtools")
                .filterByMethod(null, "trimToSize", C.Int)
                .firstOrNull()
    }

    val XmlParser: Class<*> by wxLazy("XmlParser") {
        findClassesFromPackage(wxLoader!!, wxClasses!!, "$wxPackageName.sdk.platformtools")
                .filterByMethod(C.Map, C.String, C.String)
                .firstOrNull()
    }
}