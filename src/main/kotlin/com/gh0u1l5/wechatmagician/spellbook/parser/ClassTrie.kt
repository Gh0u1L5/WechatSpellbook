package com.gh0u1l5.wechatmagician.spellbook.parser

import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList

/**
 * 用来储存一个 APK 的 package 结构
 *
 * 出于性能考虑, 这个类不支持读线程和写线程同时操作, 但支持同类型的线程同时操作
 */
class ClassTrie {
    /**
     * @suppress
     */
    private companion object {
        /**
         * 用来将 JVM 格式的类型标识符转换为类名
         *
         * Example: String 的类型标识符为 "Ljava/lang/String;"
         * Refer: https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.3
         */
        private fun convertJVMTypeToClassName(type: String) =
                type.substring(1, type.length - 1).replace('/', '.')
    }

    /**
     * 读写开关, 用于增强线程间的安全性
     *
     * 只有开关设为 true 的时候, 写操作才会被执行
     * 只有开关设为 false 的时候, 读操作才会返回有效数据
     */
    @Volatile var mutable = true

    /**
     * package 结构的根结点
     */
    private val root: TrieNode = TrieNode()

    /**
     * 插入一个单独的 JVM 格式的类型标识符
     */
    operator fun plusAssign(type: String) {
        if (mutable) {
            root.add(convertJVMTypeToClassName(type))
        }
    }

    /**
     * 插入一组 JVM 格式的类型标识符
     */
    operator fun plusAssign(types: Array<String>) {
        types.forEach { this += it }
    }

    /**
     * 查找指定包里指定深度的所有类
     *
     * 出于性能方面的考虑, 只有深度相等的类才会被返回, 比如搜索深度为0的时候, 就只返回这个包自己拥有的类, 不包括它
     * 里面其他包拥有的类.
     */
    fun search(packageName: String, depth: Int): List<String> {
        if (mutable) return emptyList()
        return root.search(packageName, depth)
    }

    /**
     * 私有的节点结构
     */
    private class TrieNode {
        val classes: MutableList<String> = ArrayList(50)

        val children: MutableMap<String, TrieNode> = ConcurrentHashMap()

        fun add(className: String) {
            add(className, 0)
        }

        private fun add(className: String, pos: Int) {
            val delimiterAt = className.indexOf('.', pos)
            if (delimiterAt == -1) {
                synchronized(this) {
                    classes.add(className)
                }
                return
            }
            val pkg = className.substring(pos, delimiterAt)
            if (pkg !in children) {
                children[pkg] = TrieNode()
            }
            children[pkg]!!.add(className, delimiterAt + 1)
        }

        fun get(depth: Int = 0): List<String> {
            if (depth == 0) return classes
            return children.flatMap { it.value.get(depth - 1) }
        }

        fun search(packageName: String, depth: Int): List<String> {
            return search(packageName, depth, 0)
        }

        private fun search(packageName: String, depth: Int, pos: Int): List<String> {
            val delimiterAt = packageName.indexOf('.', pos)
            if (delimiterAt == -1) {
                val pkg = packageName.substring(pos)
                return children[pkg]?.get(depth) ?: emptyList()
            }
            val pkg = packageName.substring(pos, delimiterAt)
            val next = children[pkg] ?: return emptyList()
            return next.search(packageName, depth, delimiterAt + 1)
        }
    }
}