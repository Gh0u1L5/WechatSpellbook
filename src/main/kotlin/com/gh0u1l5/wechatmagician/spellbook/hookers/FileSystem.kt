package com.gh0u1l5.wechatmagician.spellbook.hookers

import com.gh0u1l5.wechatmagician.spellbook.C
import com.gh0u1l5.wechatmagician.spellbook.base.EventCenter
import com.gh0u1l5.wechatmagician.spellbook.base.Hooker
import com.gh0u1l5.wechatmagician.spellbook.interfaces.IFileSystemHook
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers.findAndHookConstructor
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import java.io.File

object FileSystem : EventCenter() {

    override val interfaces: List<Class<*>>
        get() = listOf(IFileSystemHook::class.java)

    override fun provideEventHooker(event: String): Hooker? {
        return when (event) {
            "onFileDeleting", "onFileDeleted" -> onDeleteHooker
            "onFileReading" -> onReadHooker
            "onFileWriting" -> onWriteHooker
            else -> throw IllegalArgumentException("Unknown event: $event")
        }
    }

    private val onDeleteHooker = Hooker {
        findAndHookMethod(C.File, "delete", object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                val file = param.thisObject as File
                notifyWithOperation("onFileDeleting", param) { plugin ->
                    (plugin as IFileSystemHook).onFileDeleting(file)
                }
            }
            override fun afterHookedMethod(param: MethodHookParam) {
                val file   = param.thisObject as File
                val result = param.result as Boolean
                notifyWithOperation("onFileDeleted", param) { plugin ->
                    (plugin as IFileSystemHook).onFileDeleted(file, result)
                }
            }
        })
    }

    private val onReadHooker = Hooker {
        findAndHookConstructor(C.FileInputStream, C.File, object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                val file = param.args[0] as File
                notify("onFileReading") { plugin ->
                    (plugin as IFileSystemHook).onFileReading(file)
                }
            }
        })
    }

    private val onWriteHooker = Hooker {
        findAndHookConstructor(C.FileOutputStream, C.File, C.Boolean, object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                val file   = param.args[0] as File
                val append = param.args[1] as Boolean
                notify("onFileWriting") { plugin ->
                    (plugin as IFileSystemHook).onFileWriting(file, append)
                }
            }
        })
    }
}