package com.gh0u1l5.wechatmagician.spellbook.hookers

import android.os.Message
import com.gh0u1l5.wechatmagician.spellbook.C
import com.gh0u1l5.wechatmagician.spellbook.WechatStatus
import com.gh0u1l5.wechatmagician.spellbook.base.EventCenter
import com.gh0u1l5.wechatmagician.spellbook.base.Hooker
import com.gh0u1l5.wechatmagician.spellbook.interfaces.INotificationHook
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mm.booter.notification.Classes.MMNotification_MessageHandler
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers.findAndHookMethod

object Notifications : EventCenter() {

    override val interfaces: List<Class<*>>
        get() = listOf(INotificationHook::class.java)

    override fun provideEventHooker(event: String): Hooker? {
        return when (event) {
            "onMessageHandling", "onMessageHandled" -> MessageHandlerHooker
            else -> throw IllegalArgumentException("Unknown event: $event")
        }
    }

    private val MessageHandlerHooker = Hooker {
        findAndHookMethod(MMNotification_MessageHandler, "handleMessage", C.Message, object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                val raw = param.args[0] as? Message ?: return
                val talker   = raw.data.getString("notification.show.talker") ?: return
                val content  = raw.data.getString("notification.show.message.content") ?: return
                val type     = raw.data.getInt("notification.show.message.type")
                val tipsFlag = raw.data.getInt("notification.show.tipsflag")
                notifyForBypassFlags("onMessageHandling", param) { plugin ->
                    (plugin as INotificationHook).onMessageHandling(
                            INotificationHook.Message(talker, content, type, tipsFlag))
                }
            }
            override fun afterHookedMethod(param: MethodHookParam) {
                val raw = param.args[0] as? Message ?: return
                val talker   = raw.data.getString("notification.show.talker") ?: return
                val content  = raw.data.getString("notification.show.message.content") ?: return
                val type     = raw.data.getInt("notification.show.message.type")
                val tipsFlag = raw.data.getInt("notification.show.tipsflag")
                notify("onMessageHandled") { plugin ->
                    (plugin as INotificationHook).onMessageHandled(
                            INotificationHook.Message(talker, content, type, tipsFlag))
                }
            }
        })

        WechatStatus.toggle(WechatStatus.StatusFlag.STATUS_FLAG_NOTIFICATIONS)
    }
}