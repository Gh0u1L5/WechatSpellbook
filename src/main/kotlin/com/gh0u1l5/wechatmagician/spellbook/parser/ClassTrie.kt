package com.gh0u1l5.wechatmagician.spellbook.parser

import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList

class ClassTrie {
    companion object {
        private fun convertJVMTypeToClassName(type: String) =
                type.substring(1, type.length - 1).replace('/', '.')
    }

    private val head: TrieNode = TrieNode()

    operator fun plusAssign(type: String) {
        head.add(convertJVMTypeToClassName(type))
    }

    operator fun plusAssign(types: Array<String>) {
        types.forEach { this += it }
    }

    fun search(packageName: String, depth: Int): List<String> {
        return head.search(packageName, depth)
    }

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