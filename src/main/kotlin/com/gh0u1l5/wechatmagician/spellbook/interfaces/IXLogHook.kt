package com.gh0u1l5.wechatmagician.spellbook.interfaces

interface IXLogHook {
    /**
     * Called when Tencent XLog has written some log information.
     *
     * @param level a human readable log level.
     * @param tag a human readable log tag.
     * @param msg the message printed to the output.
     */
    fun onXLogWrite(level: String, tag: String, msg: String) { }
}