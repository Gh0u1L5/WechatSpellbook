package com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.mars.xlog

object Constants {
    const val Xlog_AppenderModeAsync = 0
    const val Xlog_AppenderModeSync = 1

    const val Xlog_LEVEL_ALL     = 0
    const val Xlog_LEVEL_VERBOSE = 0
    const val Xlog_LEVEL_DEBUG   = 1
    const val Xlog_LEVEL_INFO    = 2
    const val Xlog_LEVEL_WARNING = 3
    const val Xlog_LEVEL_ERROR   = 4
    const val Xlog_LEVEL_FATAL   = 5
    const val Xlog_LEVEL_NONE    = 6

    fun toHumanReadableLevel(level: Int): String = when (level) {
        Xlog_LEVEL_VERBOSE -> "VERBOSE"
        Xlog_LEVEL_DEBUG   -> "DEBUG"
        Xlog_LEVEL_INFO    -> "INFO"
        Xlog_LEVEL_WARNING -> "WARNING"
        Xlog_LEVEL_ERROR   -> "ERROR"
        Xlog_LEVEL_FATAL   -> "FATAL"
        Xlog_LEVEL_NONE    -> "NONE"
        else               -> "UNKNOWN($level)"
    }
}