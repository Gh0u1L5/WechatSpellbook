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
 * FileUtil contains the helper functions for file I/O.
 */
object FileUtil {
    /**
     * writeBytesToDisk creates a file and writes the given data into it.
     */
    fun writeBytesToDisk(path: String, content: ByteArray) {
        val file = File(path)
        file.parentFile.mkdirs()
        FileOutputStream(file).use {
            it.write(content)
        }
    }

    /**
     * readBytesFromDisk returns all the bytes of a binary file.
     */
    fun readBytesFromDisk(path: String): ByteArray {
        return FileInputStream(path).use {
            it.readBytes()
        }
    }

    /**
     * writeObjectToDisk writes a [Serializable] object onto the disk.
     */
    fun writeObjectToDisk(path: String, obj: Serializable) {
        val out = ByteArrayOutputStream()
        ObjectOutputStream(out).use {
            it.writeObject(obj)
        }
        writeBytesToDisk(path, out.toByteArray())
    }

    /**
     * readObjectFromDisk reads a [Serializable] object from the disk.
     */
    fun readObjectFromDisk(path: String): Any? {
        val bytes = readBytesFromDisk(path)
        val ins = ByteArrayInputStream(bytes)
        return ObjectInputStream(ins).use {
            it.readObject()
        }
    }

    /**
     * writeInputStreamToDisk forward the data from a [InputStream] to a file, this is extremely
     * helpful when the device has a low memory.
     *
     * @param path the path of the destination
     * @param `in` the [InputStream] that provides the data
     * @param bufferSize default buffer size, one may set a larger number for better performance.
     */
    fun writeInputStreamToDisk(path: String, `in`: InputStream, bufferSize: Int = 8192) {
        val file = File(path)
        file.parentFile.mkdirs()
        FileOutputStream(file).use {
            val buffer = ByteArray(bufferSize)
            var length = `in`.read(buffer)
            while (length != -1) {
                it.write(buffer, 0, length)
                length = `in`.read(buffer)
            }
        }
    }

    /**
     * writeBitmapToDisk saves a given [Bitmap] object to disk.
     */
    fun writeBitmapToDisk(path: String, bitmap: Bitmap) {
        val out = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        writeBytesToDisk(path, out.toByteArray())
    }

    /**
     * writeOnce ensures that the write callback will only be executed once after start up.
     */
    fun writeOnce(path: String, writeCallback: (String) -> Unit) {
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
     * createTimeTag returns the current time in a simple format as a time tag.
     */
    private val formatter = SimpleDateFormat("yyyy-MM-dd-HHmmss", Locale.getDefault())
    fun createTimeTag(): String = formatter.format(Calendar.getInstance().time)

    /**
     * notifyNewMediaFile notifies all the apps that there is a new media file to scan.
     */
    fun notifyNewMediaFile(path: String, context: Context?) {
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        context?.sendBroadcast(intent.apply {
            data = Uri.fromFile(File(path))
        })
    }
}
