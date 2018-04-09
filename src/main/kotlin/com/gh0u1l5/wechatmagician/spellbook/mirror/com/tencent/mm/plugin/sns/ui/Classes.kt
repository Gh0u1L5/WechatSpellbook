package com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.plugin.sns.ui

import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxClasses
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxLazy
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxLoader
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxPackageName
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil.findClassIfExists
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil.findClassesFromPackage

object Classes {
    val SnsActivity: Class<*> by wxLazy("SnsActivity") {
        findClassesFromPackage(wxLoader!!, wxClasses!!, "$wxPackageName.plugin.sns.ui")
                .filterByField("$wxPackageName.ui.base.MMPullDownView")
                .firstOrNull()
    }

    val SnsTimeLineUI: Class<*> by wxLazy("SnsTimeLineUI") {
        findClassesFromPackage(wxLoader!!, wxClasses!!, "$wxPackageName.plugin.sns.ui")
                .filterByField("android.support.v7.app.ActionBar")
                .firstOrNull()
    }

    val SnsUploadUI: Class<*> by wxLazy("SnsUploadUI") {
        findClassesFromPackage(wxLoader!!, wxClasses!!, "$wxPackageName.plugin.sns.ui")
                .filterByField("$wxPackageName.plugin.sns.ui.LocationWidget")
                .filterByField("$wxPackageName.plugin.sns.ui.SnsUploadSayFooter")
                .firstOrNull()
    }

    val SnsUserUI: Class<*> by wxLazy("SnsUserUI") {
        findClassIfExists("$wxPackageName.plugin.sns.ui.SnsUserUI", wxLoader!!)
    }
}