package com.gh0u1l5.wechatmagician.spellbook.parser

import com.gh0u1l5.wechatmagician.spellbook.util.ParallelUtil.parallelForEach

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

    private fun getDexFilePath(idx: Int) =
            if (idx == 1) DEX_FILE else String.format(DEX_ADDITIONAL, idx)

    private fun isDexFileExist(idx: Int): Boolean {
        val path = getDexFilePath(idx)
        return zipFile.getEntry(path) != null
    }

    val classTypes: ClassTrie by lazy {
        var last = 2
        while (isDexFileExist(last)) last++

        val ret = ClassTrie()
        (1..last).parallelForEach { idx ->
            val path = getDexFilePath(idx)
            val entry = zipFile.getEntry(path)
            val data = readEntry(entry)
            val buffer = ByteBuffer.wrap(data)
            DexParser(buffer).parseClassTypes().forEach { type ->
                ret += type
            }
        }
        return@lazy ret
    }
}