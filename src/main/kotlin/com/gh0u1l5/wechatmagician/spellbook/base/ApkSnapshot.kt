package com.gh0u1l5.wechatmagician.spellbook.base

import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil.getClassName
import net.dongliu.apk.parser.ApkFile
import net.dongliu.apk.parser.bean.DexClass
import java.io.Serializable

class ApkSnapshot(
        val versionName: String,
        val packageName: String,
        val classes: Map<String, DexClass>
): Serializable {
    companion object {
        fun convertPackageToSnapshot(apkPath: String): ApkSnapshot {
            return ApkFile(apkPath).use {
                val versionName = it.apkMeta.versionName
                val packageName = it.apkMeta.packageName
                val classes = it.dexClasses.associate {
                    getClassName(it) to it
                }
                ApkSnapshot(versionName, packageName, classes)
            }
        }
    }
}