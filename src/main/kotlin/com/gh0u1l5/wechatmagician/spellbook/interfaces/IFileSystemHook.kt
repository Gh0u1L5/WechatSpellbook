package com.gh0u1l5.wechatmagician.spellbook.interfaces

import com.gh0u1l5.wechatmagician.spellbook.base.Operation
import com.gh0u1l5.wechatmagician.spellbook.base.Operation.Companion.nop
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

interface IFileSystemHook {

    /**
     * Called when a File object is going to invoke [File.delete]
     *
     * @param file the [File] object calling [File.delete]
     * @return to bypass the original method, return a Boolean wrapped by [Operation.replacement],
     * otherwise return [Operation.nop].
     */
    fun onFileDeleting(file: File): Operation<Boolean> = nop()

    /**
     * Called when a File object has returned from [File.delete]
     *
     * @param file the [File] object calling [File.delete]
     * @param result the result returned by [File.delete]
     * @return to replace the original result, return a Boolean wrapped by [Operation.replacement],
     * otherwise return [Operation.nop].
     */
    fun onFileDeleted(file: File, result: Boolean): Operation<Boolean> = nop()

    /**
     * Called when a [FileInputStream] object is being created.
     *
     * @param file the [File] object the stream reading from.
     */
    fun onFileReading(file: File) { }

    /**
     * Called when a [FileOutputStream] object is being created.
     *
     * @param file the [File] object the stream writing to
     * @param append if `true`, then bytes will be written to the end rather than the beginning
     */
    fun onFileWriting(file: File, append: Boolean) { }
}