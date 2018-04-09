package com.gh0u1l5.wechatmagician.spellbook.util

object MirrorUtil {
    fun collectFields(instance: Any): List<Pair<String, Any>> {
        return instance::class.java.declaredFields.filter { field ->
            field.name != "INSTANCE" && field.name != "\$\$delegatedProperties"
        }.map { field ->
            field.isAccessible = true
            val key = field.name.removeSuffix("\$delegate")
            val value = field.get(instance)
            key to value
        }
    }

    fun generateReport(instances: List<Any>): List<Pair<String, String>> {
        return instances.map { instance ->
            collectFields(instance).map {
                "${instance::class.java.canonicalName}.${it.first}" to it.second.toString()
            }
        }.flatten().sortedBy { it.first }
    }

    fun generateReportWithForceEval(instances: List<Any>): List<Pair<String, String>> {
        return instances.map { instance ->
            collectFields(instance).map {
                val value = it.second
                if (value is Lazy<*>) {
                    if (!value.isInitialized()) {
                        value.value
                    }
                }
                "${instance::class.java.canonicalName}.${it.first}" to it.second.toString()
            }
        }.flatten().sortedBy { it.first }
    }
}