package com.gh0u1l5.wechatmagician.spellbook.util

import net.dongliu.apk.parser.ApkFile

object MirrorUtil {
    fun findAllMirrorObjects(apkPath: String): List<String> {
        try {
            ApkFile(apkPath).use {
                return it.dexClasses.map { dexClass ->
                    ReflectionUtil.getClassName(dexClass)
                }.filter { className ->
                    !className.last().isDigit() && // exclude anonymous classes
                            className.startsWith("com.gh0u1l5.wechatmagician.spellbook.mirror")
                }
            }
        } catch (t: Throwable) {
            return listOf()
        }
    }

    fun collectMirrorReports(objects: List<String>): List<Pair<String, String>> {
        return objects.map { className ->
            val clazz = Class.forName(className)
            val instance = clazz.getField("INSTANCE").get(null)
            clazz.declaredFields.filter { field ->
                field.name != "INSTANCE" && field.name != "\$\$delegatedProperties"
            }.map { field ->
                field.isAccessible = true
                val key = field.name.removeSuffix("\$delegate")
                val value = field.get(instance)
                "$className.$key" to value.toString()
            }
        }.flatten()
    }
}