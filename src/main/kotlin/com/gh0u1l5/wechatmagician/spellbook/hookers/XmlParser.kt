package com.gh0u1l5.wechatmagician.spellbook.hookers

import com.gh0u1l5.wechatmagician.spellbook.WechatStatus
import com.gh0u1l5.wechatmagician.spellbook.annotations.WechatHookMethod
import com.gh0u1l5.wechatmagician.spellbook.base.EventCenter
import com.gh0u1l5.wechatmagician.spellbook.interfaces.IXmlParserHook
import com.gh0u1l5.wechatmagician.spellbook.mirror.mm.sdk.platformtools.Classes.XmlParser
import com.gh0u1l5.wechatmagician.spellbook.mirror.mm.sdk.platformtools.Methods.XmlParser_parse
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil.findAndHookMethod
import de.robv.android.xposed.XC_MethodHook

object XmlParser : EventCenter() {

    override val interfaces: List<Class<*>>
        get() = listOf(IXmlParserHook::class.java)

    @Suppress("UNCHECKED_CAST")
    @WechatHookMethod @JvmStatic fun hookEvents() {
        findAndHookMethod(XmlParser, XmlParser_parse, object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                val xml  = param.args[0] as String
                val root = param.args[1] as String
                notifyWithOperation("onXmlParsing", param) { plugin ->
                    (plugin as IXmlParserHook).onXmlParsing(xml, root)
                }
            }
            override fun afterHookedMethod(param: MethodHookParam) {
                val xml    = param.args[0] as String
                val root   = param.args[1] as String
                val result = param.result as MutableMap<String, String>? ?: return
                notify("onXmlParsed") { plugin ->
                    (plugin as IXmlParserHook).onXmlParsed(xml, root, result)
                }
            }
        })

        WechatStatus.toggle(WechatStatus.StatusFlag.STATUS_FLAG_XML_PARSER)
    }
}