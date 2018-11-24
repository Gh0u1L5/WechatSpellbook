package com.gh0u1l5.wechatmagician.spellbook.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.SystemClock.elapsedRealtime
import java.io.*
import java.lang.System.currentTimeMillis
import java.text.SimpleDateFormat
import java.util.*

/**
 * 封装了一批关于磁盘 I/O 的方法
 */
object FileUtil {
    /**
     * 将一段数据写入磁盘指定位置
     *
     * 若文件及其所在目录不存在的话, 会尝试建立该文件和目录
     */
    @JvmStatic fun writeBytesToDisk(path: String, content: ByteArray) {
        val file = File(path).also { it.parentFile.mkdirs() }
        val fout = FileOutputStream(file)
        BufferedOutputStream(fout).use { it.write(content) }
    }

    /**
     * 从磁盘上读取一个文件的全部数据
     */
    @JvmStatic fun readBytesFromDisk(path: String): ByteArray {
        val fin = FileInputStream(path)
        return BufferedInputStream(fin).use { it.readBytes() }
    }

    /**
     * 将一个 [Serializable] 对象写入磁盘指定位置
     */
    @JvmStatic fun writeObjectToDisk(path: String, obj: Serializable) {
        val out = ByteArrayOutputStream()
        ObjectOutputStream(out).use {
            it.writeObject(obj)
        }
        writeBytesToDisk(path, out.toByteArray())
    }

    /**
     * 从磁盘上读取一个 [Serializable] 对象
     */
    @JvmStatic fun readObjectFromDisk(path: String): Any? {
        val bytes = readBytesFromDisk(path)
        val ins = ByteArrayInputStream(bytes)
        return ObjectInputStream(ins).use {
            it.readObject()
        }
    }

    /**
     * 将一个 [InputStream] 的内容写入磁盘指定位置
     *
     * 该函数会同步进行读写, 比较节约内存, 在内存空间不足的设备上非常有帮助
     *
     * @param path 数据保存路径
     * @param ins 提供数据的 [InputStream] 对象
     * @param bufferSize 缓冲区大小, 默认值为8192, 设置更大的值可以换来线性的性能提升
     */
    @JvmStatic fun writeInputStreamToDisk(path: String, ins: InputStream, bufferSize: Int = 8192) {
        val file = File(path)
        file.parentFile.mkdirs()
        val fout = FileOutputStream(file)
        BufferedOutputStream(fout).use {
            val buffer = ByteArray(bufferSize)
            var length = ins.read(buffer)
            while (length != -1) {
                it.write(buffer, 0, length)
                length = ins.read(buffer)
            }
        }
    }

    /**
     * 将一张 [Bitmap] 写入磁盘指定位置
     */
    @JvmStatic fun writeBitmapToDisk(path: String, bitmap: Bitmap) {
        val out = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        writeBytesToDisk(path, out.toByteArray())
    }

    /**
     * 如果指定文件开机后还没被修改过, 那么执行一次写操作
     *
     * @param writeCallback 实际进行写操作的回调函数
     */
    @JvmStatic inline fun writeOnce(path: String, writeCallback: (String) -> Unit) {
        val file = File(path)
        if (!file.exists()) {
            writeCallback(path)
            return
        }
        val bootAt = currentTimeMillis() - elapsedRealtime()
        val modifiedAt = file.lastModified()
        if (modifiedAt < bootAt) {
            writeCallback(path)
        }
    }

    /**
     * 基于当前时间创建一个时间戳
     */
    @JvmStatic fun createTimeTag(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd-HHmmss", Locale.getDefault())
        return formatter.format(Calendar.getInstance().time)
    }

    /**
     * 广播告知所有应用: 磁盘上添加了新的图片
     */
    @JvmStatic fun notifyNewMediaFile(path: String, context: Context?) {
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        context?.sendBroadcast(intent.apply {
            data = Uri.fromFile(File(path))
        })
    }
}
