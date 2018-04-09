package com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm

import com.gh0u1l5.wechatmagician.spellbook.C
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxClasses
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxLazy
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxLoader
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxPackageName
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.sdk.platformtools.Classes.LruCache
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil.findClassesFromPackage

object Classes {
    val ImgInfoStorage: Class<*> by wxLazy("ImgInfoStorage") {
        findClassesFromPackage(wxLoader!!, wxClasses!!, wxPackageName, 1)
                .filterByMethod(C.String, C.String, C.String, C.String, C.Boolean)
                .firstOrNull()
    }

    val LruCacheWithListener: Class<*> by wxLazy("LruCacheWithListener") {
        findClassesFromPackage(wxLoader!!, wxClasses!!, wxPackageName, 1)
                .filterBySuper(LruCache)
                .firstOrNull()
    }
}