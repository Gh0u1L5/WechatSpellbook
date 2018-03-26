package com.gh0u1l5.wechatmagician.spellbook.hookers

import com.gh0u1l5.wechatmagician.spellbook.base.EventCenter
import com.gh0u1l5.wechatmagician.spellbook.base.Hooker
import com.gh0u1l5.wechatmagician.spellbook.interfaces.IImageStorageHook
import com.gh0u1l5.wechatmagician.spellbook.interfaces.IMessageStorageHook
import com.gh0u1l5.wechatmagician.spellbook.mirror.mm.Classes.ImgInfoStorage
import com.gh0u1l5.wechatmagician.spellbook.mirror.mm.Methods.ImgInfoStorage_load
import com.gh0u1l5.wechatmagician.spellbook.mirror.mm.storage.Classes.MsgInfoStorage
import com.gh0u1l5.wechatmagician.spellbook.mirror.mm.storage.Methods.MsgInfoStorage_insert
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil.findAndHookMethod
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge.hookAllConstructors
import de.robv.android.xposed.XposedHelpers.getLongField

object Storage : EventCenter() {

    override val interfaces: List<Class<*>>
        get() = listOf(IMessageStorageHook::class.java, IImageStorageHook::class.java)

    override fun provideEventHooker(event: String): Hooker? {
        return when (event) {
            "onMessageStorageCreated" -> onMessageStorageCreateHooker
            "onMessageStorageInserting", "onMessageStorageInserted" -> onMessageStorageInsertHooker
            "onImageStorageCreated" -> onImageStorageCreateHooker
            "onImageStorageLoading", "onImageStorageLoaded" -> onImageStorageLoadHooker
            else -> throw IllegalArgumentException("Unknown event: $event")
        }
    }

    private val onMessageStorageCreateHooker = Hooker {
        hookAllConstructors(MsgInfoStorage, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                notify("onMessageStorageCreated") { plugin ->
                    (plugin as IMessageStorageHook).onMessageStorageCreated(param.thisObject)
                }
            }
        })
    }

    private val onMessageStorageInsertHooker = Hooker {
        findAndHookMethod(MsgInfoStorage, MsgInfoStorage_insert, object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                val msgObject = param.args[0]
                val msgId = getLongField(msgObject, "field_msgId")
                notifyWithInterrupt("onMessageStorageInserting", param) { plugin ->
                    (plugin as IMessageStorageHook).onMessageStorageInserting(msgId, msgObject)
                }
            }
            override fun afterHookedMethod(param: MethodHookParam) {
                val msgObject = param.args[0]
                val msgId = getLongField(msgObject, "field_msgId")
                notify("onMessageStorageInserted") { plugin ->
                    (plugin as IMessageStorageHook).onMessageStorageInserted(msgId, msgObject)
                }
            }
        })
    }

    private val onImageStorageCreateHooker = Hooker {
        hookAllConstructors(ImgInfoStorage, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                notify("onImageStorageCreated") { plugin ->
                    (plugin as IImageStorageHook).onImageStorageCreated(param.thisObject)
                }
            }
        })
    }

    private val onImageStorageLoadHooker = Hooker {
        findAndHookMethod(ImgInfoStorage, ImgInfoStorage_load, object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                val imageId = param.args[0] as String?
                val prefix = param.args[1] as String?
                val suffix = param.args[2] as String?
                notifyWithInterrupt("onImageStorageLoading", param) { plugin ->
                    (plugin as IImageStorageHook).onImageStorageLoading(imageId, prefix, suffix)
                }
            }
            override fun afterHookedMethod(param: MethodHookParam) {
                val imageId = param.args[0] as String?
                val prefix = param.args[1] as String?
                val suffix = param.args[2] as String?
                notify("onImageStorageLoaded") { plugin ->
                    (plugin as IImageStorageHook).onImageStorageLoaded(imageId, prefix, suffix)
                }
            }
        })
    }
}
