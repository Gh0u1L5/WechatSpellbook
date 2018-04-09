package com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.plugin.gallery.ui

import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxLazy
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxLoader
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxPackageName
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil.findClassIfExists

object Classes {
    /**
     * 当用户需要从手机相册中选择照片时启动的Activity
     */
    val AlbumPreviewUI: Class<*> by wxLazy("AlbumPreviewUI") {
        findClassIfExists("$wxPackageName.plugin.gallery.ui.AlbumPreviewUI", wxLoader!!)
    }
}