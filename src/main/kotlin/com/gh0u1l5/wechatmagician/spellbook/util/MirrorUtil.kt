package com.gh0u1l5.wechatmagician.spellbook.util

import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal

/**
 * 封装了一批用于检查“自动适配表达式”的函数
 */
object MirrorUtil {
    /**
     * 返回一个 Object 所声明的所有成员变量（不含基类成员）
     */
    @JvmStatic fun collectFields(instance: Any): List<Pair<String, Any>> {
        return instance::class.java.declaredFields.filter { field ->
            field.name != "INSTANCE" && field.name != "\$\$delegatedProperties"
        }.map { field ->
            field.isAccessible = true
            val key = field.name.removeSuffix("\$delegate")
            val value = field.get(instance)
            key to value
        }
    }

    /**
     * 生成一份适配报告, 记录每个自动适配表达式最终指向了微信中的什么位置
     */
    @JvmStatic fun generateReport(instances: List<Any>): List<Pair<String, String>> {
        return instances.map { instance ->
            collectFields(instance).map {
                "${instance::class.java.canonicalName}.${it.first}" to it.second.toString()
            }
        }.flatten().sortedBy { it.first }
    }

    /**
     * 将一个用于单元测试的惰性求值对象还原到未求值的状态
     *
     * WARN: 仅供单元测试使用
     */
    @JvmStatic fun clearUnitTestLazyFields(instance: Any) {
        instance::class.java.declaredFields.forEach { field ->
            if (Lazy::class.java.isAssignableFrom(field.type)) {
                field.isAccessible = true
                val lazyObject = field.get(instance)
                if (lazyObject is WechatGlobal.UnitTestLazyImpl<*>) {
                    lazyObject.refresh()
                }
            }
        }
    }

    /**
     * 生成一份适配报告, 记录每个自动适配表达式最终指向了微信中的什么位置
     *
     * 如果某个自动适配表达式还没有进行求值的话, 该函数会强制进行一次求值
     *
     * WARN: 仅供单元测试使用
     */
    @JvmStatic fun generateReportWithForceEval(instances: List<Any>): List<Pair<String, String>> {
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
        }.flatten() // 为了 Benchmark 的准确性, 不对结果进行排序
    }
}