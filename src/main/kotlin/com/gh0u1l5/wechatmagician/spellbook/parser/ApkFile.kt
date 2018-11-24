package com.gh0u1l5.wechatmagician.spellbook.parser

import java.io.Closeable
import java.io.File
import java.nio.ByteBuffer
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

@ExperimentalUnsignedTypes
class ApkFile(apkFile: File) : Closeable {
    companion object {
        const val DEX_FILE = "classes.dex"
        const val DEX_ADDITIONAL = "classes%d.dex"
    }

    constructor(path: String) : this(File(path))

    private val zipFile: ZipFile = ZipFile(apkFile)

    private fun readEntry(entry: ZipEntry): ByteArray =
            zipFile.getInputStream(entry).use { it.readBytes() }

    override fun close() =
            zipFile.close()

    val classTypes: Array<String> by lazy {
        var ret = emptyArray<String>()
        for (i in 1 until 1000) {
            val path = if (i == 1) DEX_FILE else String.format(DEX_ADDITIONAL, i)
            val entry = zipFile.getEntry(path) ?: break
            val buffer = ByteBuffer.wrap(readEntry(entry))
            ret += DexParser(buffer).parseClassTypes()
        }
        return@lazy ret
    }
}