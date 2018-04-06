package com.gh0u1l5.wechatmagician.spellbook.mirror

import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil
import dalvik.system.PathClassLoader
import net.dongliu.apk.parser.ApkFile
import java.io.Serializable

class ApkSnapshot(
        val versionName: String,
        val packageName: String,
        val classes: Map<String, Class<*>>
): Serializable {
    companion object {
        fun convertPackageToSnapshot(apkPath: String): ApkSnapshot {
            return ApkFile(apkPath).use {
                val versionName = it.apkMeta.versionName
                val packageName = it.apkMeta.packageName

                val loader = PathClassLoader(apkPath, ClassLoader.getSystemClassLoader())
                val classes = it.dexClasses.associate { clazz ->
                    val name = ReflectionUtil.getClassName(clazz)
                    name to loader.loadClass(name)
                }

                ApkSnapshot(versionName, packageName, classes)
            }
        }
    }

    class ApkSnapshotClassLoader(private val snapshot: ApkSnapshot): ClassLoader() {
        override fun loadClass(name: String?): Class<*> {
            return loadClass(name, false)
        }

        override fun loadClass(name: String?, resolve: Boolean): Class<*> {
            if (name in snapshot.classes) {
                return snapshot.classes[name] ?: throw ClassNotFoundException(name)
            }
            return super.loadClass(name, resolve)
        }
    }
}