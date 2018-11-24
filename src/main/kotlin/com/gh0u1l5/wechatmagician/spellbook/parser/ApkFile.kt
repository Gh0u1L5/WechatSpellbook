package com.gh0u1l5.wechatmagician.spellbook.parser

import com.gh0u1l5.wechatmagician.spellbook.util.ParallelUtil.parallelForEach

import java.io.Closeable
import java.io.File
import java.nio.ByteBuffer
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

/**
 * 封装对 APK 文件的解析操作
 *
 * 参考了 dongliu 的 apk-parser 项目
 *
 * Refer: https://github.com/hsiafan/apk-parser
 */
@ExperimentalUnsignedTypes
class ApkFile(apkFile: File) : Closeable {
    /**
     * @suppress
     */
    private companion object {
        private const val DEX_FILE = "classes.dex"
        private const val DEX_ADDITIONAL = "classes%d.dex"
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

    private fun readDexFile(idx: Int): ByteArray {
        val path = getDexFilePath(idx)
        return readEntry(zipFile.getEntry(path))
    }

    val classTypes: ClassTrie by lazy {
        var end = 2
        while (isDexFileExist(end)) end++

        val ret = ClassTrie()
        (1 until end).parallelForEach { idx ->
            val data = readDexFile(idx)
            val buffer = ByteBuffer.wrap(data)
            val parser = DexParser(buffer)
            ret += parser.parseClassTypes()
        }
        return@lazy ret.apply { mutable = false }
    }
}