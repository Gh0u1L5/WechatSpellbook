package com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.ui.conversation

import com.gh0u1l5.wechatmagician.spellbook.C
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxClasses
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxLazy
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxLoader
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxPackageName
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil.findClassesFromPackage

object Classes {
    val ConversationWithCacheAdapter: Class<*> by wxLazy("ConversationWithCacheAdapter") {
        findClassesFromPackage(wxLoader!!, wxClasses!!, "$wxPackageName.ui.conversation")
                .filterByMethod(null, "clearCache")
                .firstOrNull()
    }

    val ConversationCreateContextMenuListener: Class<*> by wxLazy("ConversationCreateContextMenuListener") {
        ConversationLongClickListener
    }

    val ConversationLongClickListener: Class<*> by wxLazy("ConversationLongClickListener") {
        findClassesFromPackage(wxLoader!!, wxClasses!!, "$wxPackageName.ui.conversation")
                .filterByMethod(null, "onCreateContextMenu", C.ContextMenu, C.View, C.ContextMenuInfo)
                .filterByMethod(C.Boolean, "onItemLongClick", C.AdapterView, C.View, C.Int, C.Long)
                .firstOrNull()
    }

    val MainUI: Class<*> by wxLazy("MainUI") {
        findClassesFromPackage(wxLoader!!, wxClasses!!, "$wxPackageName.ui.conversation")
                .filterByMethod(C.Int, "getLayoutId")
                .filterByMethod(null, "onConfigurationChanged", C.Configuration)
                .firstOrNull()
    }
}