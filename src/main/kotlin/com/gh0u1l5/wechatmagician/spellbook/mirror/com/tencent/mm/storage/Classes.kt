package com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.storage

import com.gh0u1l5.wechatmagician.spellbook.C
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxClasses
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxLazy
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxLoader
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxPackageName
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil.findClassesFromPackage

object Classes {
    val MsgInfo: Class<*> by wxLazy("MsgInfo") {
        findClassesFromPackage(wxLoader!!, wxClasses!!, "$wxPackageName.storage")
                .filterByMethod(C.Boolean, "isSystem")
                .firstOrNull()
    }

    val MsgInfoStorage: Class<*> by wxLazy("MsgInfoStorage") {
        findClassesFromPackage(wxLoader!!, wxClasses!!, "$wxPackageName.storage")
                .filterByMethod(C.Long, MsgInfo, C.Boolean)
                .firstOrNull()
    }

    val ContactInfo: Class<*> by wxLazy("ContactInfo") {
        findClassesFromPackage(wxLoader!!, wxClasses!!, "$wxPackageName.storage")
                .filterByMethod(C.String, "getCityCode")
                .filterByMethod(C.String, "getCountryCode")
                .firstOrNull()
    }
}