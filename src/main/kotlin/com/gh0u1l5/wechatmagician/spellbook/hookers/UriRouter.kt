package com.gh0u1l5.wechatmagician.spellbook.hookers

import android.app.Activity
import android.content.Intent
import com.gh0u1l5.wechatmagician.spellbook.WechatStatus
import com.gh0u1l5.wechatmagician.spellbook.base.EventCenter
import com.gh0u1l5.wechatmagician.spellbook.base.Hooker
import com.gh0u1l5.wechatmagician.spellbook.interfaces.IUriRouterHook
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.plugin.base.stub.Methods.WXCustomScheme_entry
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge.hookMethod

object UriRouter : EventCenter() {

    override val interfaces: List<Class<*>>
        get() = listOf(IUriRouterHook::class.java)

    override fun provideEventHooker(event: String): Hooker? {
        return when (event) {
            "onReceiveUri" -> UriRouterHooker
            else -> throw IllegalArgumentException("Unknown event: $event")
        }
    }

    private val UriRouterHooker = Hooker {
        hookMethod(WXCustomScheme_entry, object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                val intent = param.args[0] as Intent?
                val uri = intent?.data ?: return
                val activity = param.thisObject as Activity
                if (uri.host == "magician") {
                    notifyParallel("onReceiveUri") { plugin ->
                        (plugin as IUriRouterHook).onReceiveUri(activity, uri)
                    }
                    param.result = false
                }
            }
        })

        WechatStatus.toggle(WechatStatus.StatusFlag.STATUS_FLAG_URI_ROUTER)
    }
}