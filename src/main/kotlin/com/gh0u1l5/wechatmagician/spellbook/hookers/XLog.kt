package com.gh0u1l5.wechatmagician.spellbook.hookers

import com.gh0u1l5.wechatmagician.spellbook.base.EventCenter
import com.gh0u1l5.wechatmagician.spellbook.base.Hooker
import com.gh0u1l5.wechatmagician.spellbook.interfaces.IXLogHook
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mars.xlog.Constants.toHumanReadableLevel
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mars.xlog.Methods.Xlog_logWrite2
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge.hookMethod

object XLog : EventCenter() {
    override val interfaces: List<Class<*>>
        get() = listOf(IXLogHook::class.java)

    override fun provideEventHooker(event: String) = when (event) {
        "onXLogWrite" -> onXLogWriteHooker
        else -> throw IllegalArgumentException("Unknown event: $event")
    }

    private val onXLogWriteHooker = Hooker {
        hookMethod(Xlog_logWrite2, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val level = toHumanReadableLevel(param.args[0] as Int)
                val tag   = param.args[1] as String? ?: ""
                val msg   = param.args[8] as String? ?: ""
                notifyParallel("onXLogWrite", { plugin ->
                    (plugin as IXLogHook).onXLogWrite(level, tag, msg)
                })
            }
        })
    }
}