package com.gh0u1l5.wechatmagician.spellbook.parser

import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * 封装对 DEX 格式数据的解析操作
 *
 * 参考了 dongliu 的 apk-parser 项目
 *
 * Refer: https://github.com/hsiafan/apk-parser
 */
@ExperimentalUnsignedTypes
class DexParser(buffer: ByteBuffer) {
    private val buffer: ByteBuffer = buffer.duplicate().apply {
        order(ByteOrder.LITTLE_ENDIAN)
    }

    private fun ByteBuffer.readBytes(size: Int) = ByteArray(size).also { get(it) }

    fun parseClassTypes(): Array<String> {
        // read magic
        val magic = String(buffer.readBytes(8))
        if (!magic.startsWith("dex\n")) {
            return arrayOf()
        }
        val version = Integer.parseInt(magic.substring(4, 7))
        // now the version is 035
        if (version < 35) {
            // version 009 was used for the M3 releases of the Android platform (November–December 2007),
            // and version 013 was used for the M5 releases of the Android platform (February–March 2008)
            throw Exception("Dex file version: $version is not supported")
        }

        // read header
        val header = readDexHeader()
        header.version = version

        // read string offsets
        val stringOffsets = readStringOffsets(header.stringIdsOff, header.stringIdsSize)

        // read type ids
        val typeIds = readTypeIds(header.typeIdsOff, header.typeIdsSize)

        // read class ids
        val classIds = readClassIds(header.classDefsOff, header.classDefsSize)

        // read class types
        return Array(classIds.size) { i ->
            val classId = classIds[i]
            val typeId = typeIds[classId]
            val offset = stringOffsets[typeId]
            readStringAtOffset(offset)
        }
    }

    private fun readDexHeader() = DexHeader().apply {
        checksum = buffer.int.toUInt()

        buffer.get(signature)

        fileSize = buffer.int.toUInt()
        headerSize = buffer.int.toUInt()

        endianTag = buffer.int.toUInt()

        linkSize = buffer.int.toUInt()
        linkOff = buffer.int.toUInt()

        mapOff = buffer.int.toUInt()

        stringIdsSize = buffer.int
        stringIdsOff = buffer.int.toUInt()

        typeIdsSize = buffer.int
        typeIdsOff = buffer.int.toUInt()

        protoIdsSize = buffer.int
        protoIdsOff = buffer.int.toUInt()

        fieldIdsSize = buffer.int
        fieldIdsOff = buffer.int.toUInt()

        methodIdsSize = buffer.int
        methodIdsOff = buffer.int.toUInt()

        classDefsSize = buffer.int
        classDefsOff = buffer.int.toUInt()

        dataSize = buffer.int
        dataOff = buffer.int.toUInt()
    }

    private fun readStringOffsets(stringIdsOff: UInt, stringIdsSize: Int): IntArray {
        (buffer as Buffer).position(stringIdsOff.toInt())
        return IntArray(stringIdsSize) {
            buffer.int
        }
    }

    private fun readTypeIds(typeIdsOff: UInt, typeIdsSize: Int): IntArray {
        (buffer as Buffer).position(typeIdsOff.toInt())
        return IntArray(typeIdsSize) {
            buffer.int
        }
    }

    private fun readClassIds(classDefsOff: UInt, classDefsSize: Int): Array<Int> {
        (buffer as Buffer).position(classDefsOff.toInt())
        return Array(classDefsSize) {
            val classIdx = buffer.int
            // access_flags, skip
            buffer.int
            // superclass_idx, skip
            buffer.int
            // interfaces_off, skip
            buffer.int
            // source_file_idx, skip
            buffer.int
            // annotations_off, skip
            buffer.int
            // class_data_off, skip
            buffer.int
            // static_values_off, skip
            buffer.int

            classIdx
        }
    }

    private fun readStringAtOffset(offset: Int): String {
        (buffer as Buffer).position(offset)
        val len = readULEB128Int()
        return readString(len)
    }

    private fun readULEB128Int(): Int {
        var ret = 0

        var count = 0
        var byte: Int
        do {
            if (count > 4) {
                throw Exception("read varints error.")
            }
            byte = buffer.get().toInt()
            ret = ret or (byte and 0x7f shl count * 7)
            count++
        } while (byte and 0x80 != 0)

        return ret
    }

    private fun readString(len: Int): String {
        val chars = CharArray(len)

        for (i in 0 until len) {
            val a = buffer.get().toInt()
            when {
                a and 0x80 == 0 -> {    // ascii char
                    chars[i] = a.toChar()
                }
                a and 0xe0 == 0xc0 -> { // read one more
                    val b = buffer.get().toInt()
                    chars[i] = (a and 0x1F shl 6 or (b and 0x3F)).toChar()
                }
                a and 0xf0 == 0xe0 -> {
                    val b = buffer.get().toInt()
                    val c = buffer.get().toInt()
                    chars[i] = (a and 0x0F shl 12 or (b and 0x3F shl 6) or (c and 0x3F)).toChar()
                }
                else -> {
                    // throw UTFDataFormatException()
                }
            }
        }

        return String(chars)
    }
}

